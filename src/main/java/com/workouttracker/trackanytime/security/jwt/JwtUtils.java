package com.workouttracker.trackanytime.security.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // Lazy-loaded SecretKey to avoid multiple calls to `Keys.hmacShaKeyFor()`
    private SecretKey secretKey;

    // Method to get SecretKey (only initialize it once)
    private SecretKey getSecretKey() {
        if (secretKey == null) {
            secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());  // Convert the String key to SecretKey
        }
        return secretKey;
    }


    // Generate JWT token
    public String generateJwtToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .signWith(getSecretKey())  // Use the SecretKey to sign the JWT
                .compact();
    }

    // Parse and validate JWT token
    public Claims getClaimsFromJwtToken(String token) {
        JwtParser jwtParser = Jwts.parser()
                .setSigningKey(getSecretKey()) // Set the key used for verification
                .build();

        return jwtParser.parseClaimsJws(token).getBody(); // Parse the claims from the token
    }

    // Validate the JWT token
    public boolean validateJwtToken(String token) {
        try {
            Claims claims = getClaimsFromJwtToken(token); // If it parses without exception, it is valid
            return !claims.getExpiration().before(new Date()); // Check token expiration
        } catch (Exception e) {
            return false;
        }
    }

    // Extract username from JWT token
    public String getUsernameFromJwtToken(String token) {
        return getClaimsFromJwtToken(token).getSubject(); // Get the subject (username)
    }
}

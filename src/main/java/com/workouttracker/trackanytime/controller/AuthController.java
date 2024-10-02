package com.workouttracker.trackanytime.controller;

import com.workouttracker.trackanytime.dto.JwtResponse;
import com.workouttracker.trackanytime.dto.LoginRequest;
import com.workouttracker.trackanytime.dto.SignupRequest;
import com.workouttracker.trackanytime.model.User;
import com.workouttracker.trackanytime.security.jwt.JwtUtils;
import com.workouttracker.trackanytime.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(loginRequest.getEmail());

        return ResponseEntity.ok(new JwtResponse(jwt));

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest){
        // check if the user already exists
        if(userService.existByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest().body("Error: Email is already in use");
        }

        // Create a new user and encode the password
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setPassword(signupRequest.getPassword());
        user.setRole("USER");

        // Save the new user
        userService.saveUser(user);

        return ResponseEntity.ok("User registered successfully!");
    }
}

package com.workouttracker.trackanytime.service;

import com.workouttracker.trackanytime.model.User;
import com.workouttracker.trackanytime.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole("USER");
    }

    @Test
    public void testSaveUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");  // Mock password encoding
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.saveUser(user);
        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    public void testExistsByEmail_UserExists(){
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        boolean exists = userService.existByEmail("test@example.com");

        assertTrue(exists);
    }

    @Test
    public void testExistsByEmail_UserDoesNotExist(){
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        boolean exists = userService.existByEmail("nonexistent@example.com");

        assertFalse(exists);
    }
}

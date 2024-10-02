package com.workouttracker.trackanytime.service;

import com.workouttracker.trackanytime.model.User;
import com.workouttracker.trackanytime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()){
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        User fetchedUser = user.get();
        return org.springframework.security.core.userdetails.User.builder()
                .username(fetchedUser.getEmail())
                .password(fetchedUser.getPassword())
                .roles(fetchedUser.getRole())
                .build();
    }

}

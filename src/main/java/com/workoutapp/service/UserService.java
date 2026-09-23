package com.workoutapp.service;

import com.workoutapp.dto.AuthDtos;
import com.workoutapp.model.User;
import com.workoutapp.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(AuthDtos.RegisterRequest req) {
        if (userRepository.existsByUsername(req.username)) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(req.email)) {
            throw new IllegalArgumentException("Email is already registered");
        }
        // NOTE: plain-text password storage is for demo purposes only.
        // In production, hash the password with BCrypt (spring-security-crypto)
        // before saving it.
        User user = new User(req.username, req.email, req.password);
        return userRepository.save(user);
    }

    public User login(AuthDtos.LoginRequest req) {
        User user = userRepository.findByUsername(req.username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        if (!user.getPassword().equals(req.password)) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return user;
    }
}

package com.workoutapp.controller;

import com.workoutapp.dto.AuthDtos;
import com.workoutapp.model.User;
import com.workoutapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDtos.UserResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest req) {
        User user = userService.register(req);
        return ResponseEntity.ok(new AuthDtos.UserResponse(user.getId(), user.getUsername(), user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.UserResponse> login(@Valid @RequestBody AuthDtos.LoginRequest req) {
        User user = userService.login(req);
        return ResponseEntity.ok(new AuthDtos.UserResponse(user.getId(), user.getUsername(), user.getEmail()));
    }
}

package com.workoutapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public static class RegisterRequest {
        @NotBlank @Size(min = 3, max = 50)
        public String username;

        @NotBlank @Email
        public String email;

        @NotBlank @Size(min = 6)
        public String password;
    }

    public static class LoginRequest {
        @NotBlank
        public String username;

        @NotBlank
        public String password;
    }

    public static class UserResponse {
        public Long id;
        public String username;
        public String email;

        public UserResponse(Long id, String username, String email) {
            this.id = id;
            this.username = username;
            this.email = email;
        }
    }
}

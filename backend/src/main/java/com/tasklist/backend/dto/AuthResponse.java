package com.tasklist.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String role;
    private String username;
}
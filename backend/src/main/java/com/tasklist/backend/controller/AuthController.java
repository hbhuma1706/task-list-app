package com.tasklist.backend.controller;

import com.tasklist.backend.dto.*;
import com.tasklist.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody RefreshRequest request) {
        String accessToken = authHeader.substring(7);
        authService.logout(accessToken, request.getRefreshToken());
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
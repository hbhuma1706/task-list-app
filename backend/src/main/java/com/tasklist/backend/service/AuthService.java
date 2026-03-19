package com.tasklist.backend.service;

import com.tasklist.backend.dto.*;
import com.tasklist.backend.entity.BlacklistedToken;
import com.tasklist.backend.entity.RefreshToken;
import com.tasklist.backend.entity.User;
import com.tasklist.backend.repository.BlacklistedTokenRepository;
import com.tasklist.backend.repository.RefreshTokenRepository;
import com.tasklist.backend.repository.UserRepository;
import com.tasklist.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Revoke all previous refresh tokens
        refreshTokenRepository.revokeAllUserTokens(user);

        // Generate tokens
        String accessToken = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        String refreshTokenStr = jwtUtil.generateRefreshToken(user.getUsername());

        // Save refresh token in DB
        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshTokenStr)
                .user(user)
                .revoked(false)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .build());

        return new AuthResponse(accessToken, refreshTokenStr, user.getRole().name(), user.getUsername());
    }

    public AuthResponse refresh(RefreshRequest request) {
        String refreshTokenStr = request.getRefreshToken();

        // Validate refresh token signature
        if (!jwtUtil.isRefreshTokenValid(refreshTokenStr)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Find in DB
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        // Check if revoked
        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        // Check if expired
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired");
        }

        // Get user
        User user = refreshToken.getUser();

        // Revoke old refresh token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        // Generate new tokens
        String newAccessToken = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // Save new refresh token
        refreshTokenRepository.save(RefreshToken.builder()
                .token(newRefreshToken)
                .user(user)
                .revoked(false)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .build());

        return new AuthResponse(newAccessToken, newRefreshToken, user.getRole().name(), user.getUsername());
    }

    public void logout(String accessToken, String refreshTokenStr) {
        // Blacklist access token
        blacklistedTokenRepository.save(BlacklistedToken.builder()
                .token(accessToken)
                .blacklistedAt(LocalDateTime.now())
                .build());

        // Revoke refresh token
        refreshTokenRepository.findByToken(refreshTokenStr)
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                });
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }
}
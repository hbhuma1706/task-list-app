package com.tasklist.backend.config;

import com.tasklist.backend.entity.Role;
import com.tasklist.backend.entity.User;
import com.tasklist.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build());
        }
        if (userRepository.findByUsername("viewer").isEmpty()) {
            userRepository.save(User.builder()
                    .username("viewer")
                    .password(passwordEncoder.encode("viewer123"))
                    .role(Role.READ_ONLY)
                    .build());
        }
    }
}
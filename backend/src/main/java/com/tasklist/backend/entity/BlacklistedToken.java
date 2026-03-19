package com.tasklist.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blacklisted_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 1000)
    private String token;

    @Column(nullable = false)
    private LocalDateTime blacklistedAt;
}

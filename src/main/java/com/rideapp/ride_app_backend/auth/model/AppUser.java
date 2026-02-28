package com.rideapp.ride_app_backend.auth.model;

import com.rideapp.ride_app_backend.auth.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter @Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=80)
    private String email;

    @Column(nullable=false, unique=true, length=40)
    private String username;

    @Column(name="password_hash", nullable=false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private RoleName role = RoleName.USER;

    @Column(nullable=false)
    private Boolean enabled = true;

    @Column(nullable=false, updatable=false)
    private Instant createdAt = Instant.now();
}
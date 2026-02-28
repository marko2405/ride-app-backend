package com.rideapp.ride_app_backend.auth.service.implementation;


import com.rideapp.ride_app_backend.auth.dto.AuthResponse;
import com.rideapp.ride_app_backend.auth.dto.LoginRequest;
import com.rideapp.ride_app_backend.auth.dto.RegisterRequest;
import com.rideapp.ride_app_backend.auth.enums.RoleName;
import com.rideapp.ride_app_backend.auth.model.AppUser;
import com.rideapp.ride_app_backend.auth.repository.UserRepository;
import com.rideapp.ride_app_backend.auth.security.JwtService;
import com.rideapp.ride_app_backend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest request) {

        String email = normalizeEmail(request.getEmail());
        String username = request.getUsername().trim();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already in use");
        }

        RoleName role = RoleName.USER;
        if ("DRIVER".equalsIgnoreCase(request.getDesiredRole())) {
            role = RoleName.DRIVER;
        }

        AppUser user = new AppUser();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setEnabled(true);

        AppUser savedUser = userRepository.save(user);

        String token = jwtService.generateToken(
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getId()
        );

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getUsername(),
                savedUser.getRole().name(),
                savedUser.getEnabled()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        String email = normalizeEmail(request.getEmail());

        Optional<AppUser> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        AppUser user = optionalUser.get();

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new IllegalArgumentException("User is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name(),
                user.getEnabled()
        );
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }
}

package com.rideapp.ride_app_backend.auth.service.implementation;
import com.rideapp.ride_app_backend.auth.dto.AuthResponse;
import com.rideapp.ride_app_backend.auth.dto.LoginRequest;
import com.rideapp.ride_app_backend.auth.dto.RegisterRequest;
import com.rideapp.ride_app_backend.auth.security.JwtService;
import com.rideapp.ride_app_backend.auth.service.AuthService;
import com.rideapp.ride_app_backend.common.enums.Role;
import com.rideapp.ride_app_backend.user.entity.DriverProfile;
import com.rideapp.ride_app_backend.user.entity.User;
import com.rideapp.ride_app_backend.user.repository.DriverProfileRepository;
import com.rideapp.ride_app_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final DriverProfileRepository driverProfileRepository;

    @Override
    public AuthResponse register(RegisterRequest request) {

        String email = normalizeEmail(request.getEmail());
        String username = request.getUsername().trim();
        String firstName = request.getFirstName().trim();
        String lastName = request.getLastName().trim();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already in use");
        }

        if (request.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Admin cannot be registered publicly");
        }

        if (request.getRole() == Role.DRIVER) {
            validateDriverRegistrationFields(request);
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(true);
        user.setCreatedAt(Instant.now());

        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == Role.DRIVER) {
            DriverProfile driverProfile = DriverProfile.builder()
                    .user(savedUser)
                    .licenseNumber(request.getLicenseNumber())
                    .yearsOfExperience(request.getYearsOfExperience())
                    .active(true)
                    .averageRating(0.0)
                    .totalRatings(0)
                    .vehicleClass(request.getVehicleClass())
                    .carBrand(request.getCarBrand())
                    .carModel(request.getCarModel())
                    .carColor(request.getCarColor())
                    .plateNumber(request.getPlateNumber())
                    .seats(request.getSeats())
                    .build();

            driverProfileRepository.save(driverProfile);
        }

        String token = jwtService.generateToken(
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getId()
        );

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getUsername(),
                savedUser.getRole().name(),
                savedUser.getEnabled()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        String email = normalizeEmail(request.getEmail());

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        User user = optionalUser.get();

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
                user.getFirstName(),
                user.getLastName(),
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
    private void validateDriverRegistrationFields(RegisterRequest request) {
        if (request.getVehicleClass() == null) {
            throw new IllegalArgumentException("Vehicle class is required for driver registration");
        }

        if (request.getCarBrand() == null || request.getCarBrand().trim().isBlank()) {
            throw new IllegalArgumentException("Car brand is required for driver registration");
        }

        if (request.getCarModel() == null || request.getCarModel().trim().isBlank()) {
            throw new IllegalArgumentException("Car model is required for driver registration");
        }

        if (request.getCarColor() == null || request.getCarColor().trim().isBlank()) {
            throw new IllegalArgumentException("Car color is required for driver registration");
        }

        if (request.getPlateNumber() == null || request.getPlateNumber().trim().isBlank()) {
            throw new IllegalArgumentException("Plate number is required for driver registration");
        }

        if (request.getSeats() == null || request.getSeats() < 1) {
            throw new IllegalArgumentException("Seats must be greater than 0");
        }
    }
}

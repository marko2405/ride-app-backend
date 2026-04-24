package com.rideapp.ride_app_backend.user.repository;

import com.rideapp.ride_app_backend.user.entity.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
    Optional<DriverProfile> findByUserId(Long userId);

    Optional<DriverProfile> findByUserEmail(String email);
}

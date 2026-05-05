package com.rideapp.ride_app_backend.user.repository;

import com.rideapp.ride_app_backend.user.entity.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
    Optional<DriverProfile> findByUserId(Long userId);

    Optional<DriverProfile> findByUserEmail(String email);

    long countByActive(Boolean active);

    @Query("""
    select dp
    from DriverProfile dp
    join fetch dp.user
""")
    List<DriverProfile> findAllWithUser();
}

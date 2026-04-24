package com.rideapp.ride_app_backend.user.entity;

import com.rideapp.ride_app_backend.common.enums.VehicleClass;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "driver_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "license_number", length = 100)
    private String licenseNumber;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "average_rating", nullable = false)
    private Double averageRating = 0.0;

    @Column(name = "total_ratings", nullable = false)
    private Integer totalRatings = 0;
    @Enumerated(EnumType.STRING)

    @Column(name = "vehicle_class")
    private VehicleClass vehicleClass;

    @Column(name = "car_brand", length = 50)
    private String carBrand;

    @Column(name = "car_model", length = 50)
    private String carModel;

    @Column(name = "car_color", length = 30)
    private String carColor;

    @Column(name = "plate_number", length = 20, unique = true)
    private String plateNumber;

    @Column(name = "seats")
    private Integer seats;
}
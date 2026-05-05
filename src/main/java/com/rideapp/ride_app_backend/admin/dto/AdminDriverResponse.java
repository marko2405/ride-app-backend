package com.rideapp.ride_app_backend.admin.dto;

import com.rideapp.ride_app_backend.common.enums.VehicleClass;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDriverResponse {
    private Long userId;
    private Long driverProfileId;

    private String firstName;
    private String lastName;
    private String email;
    private String username;

    private Boolean enabled;
    private Boolean active;

    private VehicleClass vehicleClass;
    private String licenseNumber;
    private Integer yearsOfExperience;

    private Double averageRating;
    private Integer totalRatings;

    private String carBrand;
    private String carModel;
    private String carColor;
    private String plateNumber;
    private Integer seats;
}
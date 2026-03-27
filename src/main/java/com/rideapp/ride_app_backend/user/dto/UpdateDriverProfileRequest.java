package com.rideapp.ride_app_backend.user.dto;

import lombok.Data;

@Data
public class UpdateDriverProfileRequest {

    private String licenseNumber;
    private Integer yearsOfExperience;
}

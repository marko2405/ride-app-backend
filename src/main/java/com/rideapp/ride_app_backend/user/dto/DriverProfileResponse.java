package com.rideapp.ride_app_backend.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverProfileResponse {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String licenseNumber;
    private Integer yearsOfExperience;
    private Boolean active;
    private Double averageRating;
    private Integer totalRatings;
}

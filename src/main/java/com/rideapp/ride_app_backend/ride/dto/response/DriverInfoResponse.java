package com.rideapp.ride_app_backend.ride.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DriverInfoResponse {
    private Long id;
    private String fullName;
    private Double averageRating;
    private Integer totalRatings;
}

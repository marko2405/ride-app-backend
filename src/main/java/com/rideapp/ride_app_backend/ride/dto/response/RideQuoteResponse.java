package com.rideapp.ride_app_backend.ride.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RideQuoteResponse {
    private Long distanceMeters;
    private Long durationSeconds;
    private String currency;
    private List<RideOptionResponse> options;
}

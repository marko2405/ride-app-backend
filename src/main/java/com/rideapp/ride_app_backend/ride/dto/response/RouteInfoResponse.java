package com.rideapp.ride_app_backend.ride.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RouteInfoResponse {
    private Long distanceMeters;
    private Long durationSeconds;
}

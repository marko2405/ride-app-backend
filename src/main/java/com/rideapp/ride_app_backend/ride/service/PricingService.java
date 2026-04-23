package com.rideapp.ride_app_backend.ride.service;

import com.rideapp.ride_app_backend.ride.dto.response.RideQuoteResponse;

public interface PricingService {
    RideQuoteResponse calculateQuote(Long distanceMeters, Long durationSeconds);
}
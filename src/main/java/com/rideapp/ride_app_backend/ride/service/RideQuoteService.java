package com.rideapp.ride_app_backend.ride.service;

import com.rideapp.ride_app_backend.ride.dto.request.RideQuoteRequest;
import com.rideapp.ride_app_backend.ride.dto.response.RideQuoteResponse;

public interface RideQuoteService {
    RideQuoteResponse getQuote(RideQuoteRequest request);
}

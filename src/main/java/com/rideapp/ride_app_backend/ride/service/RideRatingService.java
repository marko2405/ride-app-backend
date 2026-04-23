package com.rideapp.ride_app_backend.ride.service;

import com.rideapp.ride_app_backend.ride.dto.request.CreateRideRatingRequest;
import com.rideapp.ride_app_backend.ride.dto.response.RideRatingResponse;
import com.rideapp.ride_app_backend.ride.dto.response.RideRatingsForRideResponse;
import com.rideapp.ride_app_backend.ride.dto.response.UserRatingSummaryResponse;

import java.util.List;

public interface RideRatingService {

    RideRatingResponse createRating(Long rideId, CreateRideRatingRequest request, String userEmail);

    RideRatingsForRideResponse getRideRatings(Long rideId, String userEmail);

    UserRatingSummaryResponse getUserRatingSummary(Long userId);

    List<RideRatingResponse> getUserRatings(Long userId);
}

package com.rideapp.ride_app_backend.ride.service;

import com.rideapp.ride_app_backend.ride.dto.request.CreateRideRequest;
import com.rideapp.ride_app_backend.ride.dto.response.RideResponse;

import java.util.List;

public interface RideService {
    RideResponse createRide(CreateRideRequest request, String userEmail);

    List<RideResponse> getMyRides(String userEmail);

    RideResponse getRideById(Long rideId, String userEmail);

    List<RideResponse> getAvailableRides(String userEmail);

    List<RideResponse> getMyDriverRides(String userEmail);

    RideResponse acceptRide(Long rideId, String userEmail);

    RideResponse startRide(Long rideId, String userEmail);

    RideResponse completeRide(Long rideId, String userEmail);

    RideResponse cancelRide(Long rideId, String userEmail);}

package com.rideapp.ride_app_backend.ride.service;

import com.rideapp.ride_app_backend.ride.dto.response.RouteInfoResponse;

public interface GoogleRoutesService {
    RouteInfoResponse getRouteInfo(Double pickupLat, Double pickupLng, Double dropoffLat, Double dropoffLng);
}

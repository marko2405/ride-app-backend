package com.rideapp.ride_app_backend.ride.service.implementation;

import com.rideapp.ride_app_backend.ride.dto.request.RideQuoteRequest;
import com.rideapp.ride_app_backend.ride.dto.response.RideQuoteResponse;
import com.rideapp.ride_app_backend.ride.dto.response.RouteInfoResponse;
import com.rideapp.ride_app_backend.ride.service.GoogleRoutesService;
import com.rideapp.ride_app_backend.ride.service.PricingService;
import com.rideapp.ride_app_backend.ride.service.RideQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideQuoteServiceImpl implements RideQuoteService {

    private final GoogleRoutesService googleRoutesService;
    private final PricingService pricingService;

    @Override
    public RideQuoteResponse getQuote(RideQuoteRequest request) {
        validateRequest(request);

        RouteInfoResponse routeInfo = googleRoutesService.getRouteInfo(
                request.getPickupLat(),
                request.getPickupLng(),
                request.getDropoffLat(),
                request.getDropoffLng()
        );

        return pricingService.calculateQuote(
                routeInfo.getDistanceMeters(),
                routeInfo.getDurationSeconds()
        );
    }

    private void validateRequest(RideQuoteRequest request) {
        if (request.getPickupLat() == null || request.getPickupLng() == null) {
            throw new IllegalArgumentException("Pickup coordinates are required.");
        }

        if (request.getDropoffLat() == null || request.getDropoffLng() == null) {
            throw new IllegalArgumentException("Dropoff coordinates are required.");
        }
    }
}

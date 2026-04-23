package com.rideapp.ride_app_backend.ride.controller;

import com.rideapp.ride_app_backend.ride.dto.request.CreateRideRatingRequest;
import com.rideapp.ride_app_backend.ride.dto.response.RideRatingResponse;
import com.rideapp.ride_app_backend.ride.dto.response.RideRatingsForRideResponse;
import com.rideapp.ride_app_backend.ride.dto.response.UserRatingSummaryResponse;
import com.rideapp.ride_app_backend.ride.service.RideRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RideRatingController {

    private final RideRatingService rideRatingService;

    @PostMapping("/rides/{rideId}/rating")
    public RideRatingResponse createRating(@PathVariable Long rideId,
                                           @Valid @RequestBody CreateRideRatingRequest request,
                                           Authentication authentication) {
        return rideRatingService.createRating(rideId, request, authentication.getName());
    }

    @GetMapping("/rides/{rideId}/rating")
    public RideRatingsForRideResponse getRideRatings(@PathVariable Long rideId,
                                                     Authentication authentication) {
        return rideRatingService.getRideRatings(rideId, authentication.getName());
    }

    @GetMapping("/users/{userId}/rating-summary")
    public UserRatingSummaryResponse getUserRatingSummary(@PathVariable Long userId) {
        return rideRatingService.getUserRatingSummary(userId);
    }

    @GetMapping("/users/{userId}/ratings")
    public List<RideRatingResponse> getUserRatings(@PathVariable Long userId) {
        return rideRatingService.getUserRatings(userId);
    }
}

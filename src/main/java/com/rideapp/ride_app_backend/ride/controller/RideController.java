package com.rideapp.ride_app_backend.ride.controller;

import com.rideapp.ride_app_backend.ride.dto.request.CreateRideRequest;
import com.rideapp.ride_app_backend.ride.dto.request.RideQuoteRequest;
import com.rideapp.ride_app_backend.ride.dto.response.RideQuoteResponse;
import com.rideapp.ride_app_backend.ride.dto.response.RideResponse;
import com.rideapp.ride_app_backend.ride.service.RideQuoteService;
import com.rideapp.ride_app_backend.ride.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideQuoteService rideQuoteService;
    private final RideService rideService;

    @PostMapping
    public RideResponse createRide(@Valid @RequestBody CreateRideRequest request,
                                   Authentication authentication) {
        return rideService.createRide(request, authentication.getName());
    }

    @GetMapping("/my")
    public List<RideResponse> getMyRides(Authentication authentication) {
        return rideService.getMyRides(authentication.getName());
    }

    @GetMapping("/driver/my")
    public List<RideResponse> getMyDriverRides(Authentication authentication) {
        return rideService.getMyDriverRides(authentication.getName());
    }

    @GetMapping("/available")
    public List<RideResponse> getAvailableRides(Authentication authentication) {
        return rideService.getAvailableRides(authentication.getName());
    }

    @GetMapping("/{rideId}")
    public RideResponse getRideById(@PathVariable Long rideId,
                                    Authentication authentication) {
        return rideService.getRideById(rideId, authentication.getName());
    }

    @PostMapping("/quote")
    public ResponseEntity<RideQuoteResponse> getQuote(@RequestBody RideQuoteRequest request) {
        return ResponseEntity.ok(rideQuoteService.getQuote(request));
    }

    @PatchMapping("/{rideId}/accept")
    public RideResponse acceptRide(@PathVariable Long rideId, Authentication authentication) {
        return rideService.acceptRide(rideId, authentication.getName());
    }

    @PatchMapping("/{rideId}/start")
    public RideResponse startRide(@PathVariable Long rideId, Authentication authentication) {
        return rideService.startRide(rideId, authentication.getName());
    }

    @PatchMapping("/{rideId}/complete")
    public RideResponse completeRide(@PathVariable Long rideId, Authentication authentication) {
        return rideService.completeRide(rideId, authentication.getName());
    }

    @PatchMapping("/{rideId}/cancel")
    public RideResponse cancelRide(@PathVariable Long rideId, Authentication authentication) {
        return rideService.cancelRide(rideId, authentication.getName());
    }
}
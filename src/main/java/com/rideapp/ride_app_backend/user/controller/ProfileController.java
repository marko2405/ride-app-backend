package com.rideapp.ride_app_backend.user.controller;

import com.rideapp.ride_app_backend.user.dto.DriverProfileResponse;
import com.rideapp.ride_app_backend.user.dto.UpdateDriverProfileRequest;
import com.rideapp.ride_app_backend.user.dto.UpdateUserProfileRequest;
import com.rideapp.ride_app_backend.user.dto.UserProfileResponse;
import com.rideapp.ride_app_backend.user.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public UserProfileResponse getMyProfile(Authentication authentication) {
        return profileService.getMyProfile(authentication.getName());
    }

    @PutMapping("/me")
    public UserProfileResponse updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        return profileService.updateMyProfile(authentication.getName(), request);
    }

    @GetMapping("/me/driver")
    public DriverProfileResponse getMyDriverProfile(Authentication authentication) {
        return profileService.getMyDriverProfile(authentication.getName());
    }

    @PutMapping("/me/driver")
    public DriverProfileResponse updateMyDriverProfile(
            Authentication authentication,
            @RequestBody UpdateDriverProfileRequest request
    ) {
        return profileService.updateMyDriverProfile(authentication.getName(), request);
    }
}

package com.rideapp.ride_app_backend.user.service;

import com.rideapp.ride_app_backend.user.dto.DriverProfileResponse;
import com.rideapp.ride_app_backend.user.dto.UpdateDriverProfileRequest;
import com.rideapp.ride_app_backend.user.dto.UpdateUserProfileRequest;
import com.rideapp.ride_app_backend.user.dto.UserProfileResponse;

public interface ProfileService {

    UserProfileResponse getMyProfile(String email);

    UserProfileResponse updateMyProfile(String email, UpdateUserProfileRequest request);

    DriverProfileResponse getMyDriverProfile(String email);

    DriverProfileResponse updateMyDriverProfile(String email, UpdateDriverProfileRequest request);
}

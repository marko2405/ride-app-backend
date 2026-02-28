package com.rideapp.ride_app_backend.auth.service;

import com.rideapp.ride_app_backend.auth.dto.AuthResponse;
import com.rideapp.ride_app_backend.auth.dto.LoginRequest;
import com.rideapp.ride_app_backend.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}

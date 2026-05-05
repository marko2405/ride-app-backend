package com.rideapp.ride_app_backend.admin.service;

import com.rideapp.ride_app_backend.admin.dto.AdminDashboardResponse;
import com.rideapp.ride_app_backend.admin.dto.AdminDriverResponse;
import com.rideapp.ride_app_backend.admin.dto.AdminUserResponse;

import java.util.List;

public interface AdminService {

    AdminDashboardResponse getDashboard();

    List<AdminUserResponse> getUsers();

    List<AdminDriverResponse> getDrivers();

    void deleteUser(Long userId);

    void deleteDriver(Long userId);

    void toggleUserStatus(Long userId);

    void toggleDriverStatus(Long userId);
}

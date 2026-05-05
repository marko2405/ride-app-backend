package com.rideapp.ride_app_backend.admin.controller;

import com.rideapp.ride_app_backend.admin.dto.AdminDashboardResponse;
import com.rideapp.ride_app_backend.admin.dto.AdminDriverResponse;
import com.rideapp.ride_app_backend.admin.dto.AdminUserResponse;
import com.rideapp.ride_app_backend.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard() {
        return adminService.getDashboard();
    }

    @GetMapping("/users")
    public List<AdminUserResponse> getUsers() {
        return adminService.getUsers();
    }

    @GetMapping("/drivers")
    public List<AdminDriverResponse> getDrivers() {
        return adminService.getDrivers();
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
    }

    @DeleteMapping("/drivers/{userId}")
    public void deleteDriver(@PathVariable Long userId) {
        adminService.deleteDriver(userId);
    }

    @PatchMapping("/users/{userId}/toggle-status")
    public void toggleUserStatus(@PathVariable Long userId) {
        adminService.toggleUserStatus(userId);
    }

    @PatchMapping("/drivers/{userId}/toggle-status")
    public void toggleDriverStatus(@PathVariable Long userId) {
        adminService.toggleDriverStatus(userId);
    }
}

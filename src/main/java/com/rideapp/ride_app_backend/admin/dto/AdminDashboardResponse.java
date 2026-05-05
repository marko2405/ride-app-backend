package com.rideapp.ride_app_backend.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AdminDashboardResponse {
    private long totalUsers;
    private long totalPassengers;
    private long totalDrivers;
    private long activeDrivers;

    private long totalRides;
    private long requestedRides;
    private long acceptedRides;
    private long inProgressRides;
    private long completedRides;
    private long cancelledRides;

    private long economicRides;
    private long businessRides;
    private long comfortRides;

    private BigDecimal totalRevenue;
}
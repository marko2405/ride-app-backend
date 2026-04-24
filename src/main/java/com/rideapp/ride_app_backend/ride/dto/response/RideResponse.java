package com.rideapp.ride_app_backend.ride.dto.response;

import com.rideapp.ride_app_backend.common.enums.RideStatus;
import com.rideapp.ride_app_backend.common.enums.VehicleClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RideResponse {

    private Long id;
    private Double pickupLat;
    private Double pickupLng;
    private Double dropoffLat;
    private Double dropoffLng;
    private VehicleClass vehicleClass;
    private RideStatus status;
    private Long distanceMeters;
    private Long durationSeconds;
    private String currency;
    private BigDecimal basePrice;
    private BigDecimal distancePrice;
    private BigDecimal totalPrice;
    private LocalDateTime scheduledFor;
    private Long passengerId;
    private Long driverId;
    private PassengerInfoResponse passengerInfo;
    private DriverInfoResponse driverInfo;
    private VehicleInfoResponse vehicleInfo;
    private Instant createdAt;
    private Instant updatedAt;
}

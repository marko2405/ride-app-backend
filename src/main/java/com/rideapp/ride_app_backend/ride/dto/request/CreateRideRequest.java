package com.rideapp.ride_app_backend.ride.dto.request;

import com.rideapp.ride_app_backend.common.enums.VehicleClass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateRideRequest {

    @NotNull(message = "Pickup latitude is required")
    private Double pickupLat;

    @NotNull(message = "Pickup longitude is required")
    private Double pickupLng;

    @NotNull(message = "Dropoff latitude is required")
    private Double dropoffLat;

    @NotNull(message = "Dropoff longitude is required")
    private Double dropoffLng;

    @NotNull(message = "Vehicle class is required")
    private VehicleClass vehicleClass;

    private LocalDateTime scheduledFor;
}

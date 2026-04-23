package com.rideapp.ride_app_backend.ride.dto.response;

import com.rideapp.ride_app_backend.common.enums.VehicleClass;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RideOptionResponse {
    private VehicleClass vehicleClass;
    private BigDecimal basePrice;
    private BigDecimal distancePrice;
    private BigDecimal totalPrice;
}

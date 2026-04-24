package com.rideapp.ride_app_backend.ride.dto.response;

import com.rideapp.ride_app_backend.common.enums.VehicleClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VehicleInfoResponse {
    private VehicleClass vehicleClass;
    private String carBrand;
    private String carModel;
    private String carColor;
    private String plateNumber;
    private Integer seats;
}

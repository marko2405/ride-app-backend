package com.rideapp.ride_app_backend.ride.service.implementation;
import com.rideapp.ride_app_backend.common.enums.VehicleClass;
import com.rideapp.ride_app_backend.ride.dto.response.RideOptionResponse;
import com.rideapp.ride_app_backend.ride.dto.response.RideQuoteResponse;
import com.rideapp.ride_app_backend.ride.service.PricingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
@Service
public class PricingServiceImpl implements PricingService {

    @Override
    public RideQuoteResponse calculateQuote(Long distanceMeters, Long durationSeconds) {
        RideOptionResponse economic = buildOption(VehicleClass.ECONOMIC, distanceMeters);
        RideOptionResponse business = buildOption(VehicleClass.BUSINESS, distanceMeters);
        RideOptionResponse comfort = buildOption(VehicleClass.COMFORT, distanceMeters);

        RideQuoteResponse response = new RideQuoteResponse();
        response.setDistanceMeters(distanceMeters);
        response.setDurationSeconds(durationSeconds);
        response.setCurrency("RSD");
        response.setOptions(List.of(economic, business, comfort));

        return response;
    }

    private RideOptionResponse buildOption(VehicleClass vehicleClass, Long distanceMeters) {
        BigDecimal basePrice;
        BigDecimal pricePerKm;

        switch (vehicleClass) {
            case BUSINESS -> {
                basePrice = BigDecimal.valueOf(250);
                pricePerKm = BigDecimal.valueOf(110);
            }
            case COMFORT -> {
                basePrice = BigDecimal.valueOf(300);
                pricePerKm = BigDecimal.valueOf(130);
            }
            case ECONOMIC -> {
                basePrice = BigDecimal.valueOf(150);
                pricePerKm = BigDecimal.valueOf(70);
            }
            default -> throw new IllegalArgumentException("Unsupported vehicle class");
        }

        BigDecimal distanceKm = BigDecimal.valueOf(distanceMeters)
                .divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);

        BigDecimal distancePrice = distanceKm.multiply(pricePerKm)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalPrice = basePrice.add(distancePrice)
                .setScale(2, RoundingMode.HALF_UP);

        RideOptionResponse option = new RideOptionResponse();
        option.setVehicleClass(vehicleClass);
        option.setBasePrice(basePrice);
        option.setDistancePrice(distancePrice);
        option.setTotalPrice(totalPrice);

        return option;
    }
}

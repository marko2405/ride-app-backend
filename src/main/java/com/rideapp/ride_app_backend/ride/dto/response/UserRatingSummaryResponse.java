package com.rideapp.ride_app_backend.ride.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class UserRatingSummaryResponse {

    private Long userId;
    private BigDecimal averageRating;
    private Long totalRatings;
    private Map<Integer, Long> breakdown;
}

package com.rideapp.ride_app_backend.ride.dto.response;

import com.rideapp.ride_app_backend.common.enums.RatingType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class RideRatingResponse {

    private Long id;
    private Long rideId;
    private Long fromUserId;
    private Long toUserId;
    private RatingType ratingType;
    private Integer score;
    private Instant createdAt;
}

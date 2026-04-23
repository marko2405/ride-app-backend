package com.rideapp.ride_app_backend.ride.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RideRatingsForRideResponse {

    private RideRatingResponse passengerToDriverRating;
    private RideRatingResponse driverToPassengerRating;
}

package com.rideapp.ride_app_backend.ride.repository;
import com.rideapp.ride_app_backend.common.enums.RideStatus;
import com.rideapp.ride_app_backend.common.enums.VehicleClass;
import com.rideapp.ride_app_backend.ride.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByPassengerIdOrderByCreatedAtDesc(Long passengerId);

    List<Ride> findByStatusAndDriverIsNullOrderByCreatedAtAsc(RideStatus status);

    List<Ride> findByStatusAndDriverIsNullAndVehicleClassOrderByCreatedAtAsc(
            RideStatus status,
            VehicleClass vehicleClass
    );

    List<Ride> findByDriverIdOrderByCreatedAtDesc(Long driverId);
}

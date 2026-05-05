package com.rideapp.ride_app_backend.ride.service.implementation;

import com.rideapp.ride_app_backend.common.enums.RideStatus;
import com.rideapp.ride_app_backend.common.enums.Role;
import com.rideapp.ride_app_backend.ride.dto.request.CreateRideRequest;
import com.rideapp.ride_app_backend.ride.dto.request.RideQuoteRequest;
import com.rideapp.ride_app_backend.ride.dto.response.*;
import com.rideapp.ride_app_backend.ride.entity.Ride;
import com.rideapp.ride_app_backend.ride.repository.RideRatingRepository;
import com.rideapp.ride_app_backend.ride.repository.RideRepository;
import com.rideapp.ride_app_backend.ride.service.RideQuoteService;
import com.rideapp.ride_app_backend.ride.service.RideService;
import com.rideapp.ride_app_backend.user.entity.DriverProfile;
import com.rideapp.ride_app_backend.user.entity.User;
import com.rideapp.ride_app_backend.user.repository.DriverProfileRepository;
import com.rideapp.ride_app_backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRatingRepository rideRatingRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final RideQuoteService rideQuoteService;
    private final DriverProfileRepository driverProfileRepository;
    public static final String USER_NOT_FOUND  = "User not found";
    public static final String RIDE_NOT_FOUND = "Ride not found";
    public static final String DRIVER_NOT_FOUND  = "Driver profile not found";

    @Override
    @Transactional
    public RideResponse createRide(CreateRideRequest request, String userEmail) {
        validateRideAddresses(request);
        User passenger = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND ));
        RideQuoteRequest quoteRequest = new RideQuoteRequest();
        quoteRequest.setPickupLat(request.getPickupLat());
        quoteRequest.setPickupLng(request.getPickupLng());
        quoteRequest.setDropoffLat(request.getDropoffLat());
        quoteRequest.setDropoffLng(request.getDropoffLng());

        RideQuoteResponse quoteResponse = rideQuoteService.getQuote(quoteRequest);

        RideOptionResponse selectedOption = quoteResponse.getOptions()
                .stream()
                .filter(option -> option.getVehicleClass() == request.getVehicleClass())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Selected vehicle class not found in quote response"));

        Ride ride = new Ride();
        ride.setPickupLat(request.getPickupLat());
        ride.setPickupLng(request.getPickupLng());
        ride.setPickupAddress(request.getPickupAddress());
        ride.setDropoffLat(request.getDropoffLat());
        ride.setDropoffLng(request.getDropoffLng());
        ride.setDropoffAddress(request.getDropoffAddress());
        ride.setVehicleClass(request.getVehicleClass());
        ride.setStatus(RideStatus.REQUESTED);
        ride.setDistanceMeters(quoteResponse.getDistanceMeters());
        ride.setDurationSeconds(quoteResponse.getDurationSeconds());
        ride.setCurrency(quoteResponse.getCurrency());
        ride.setBasePrice(selectedOption.getBasePrice());
        ride.setDistancePrice(selectedOption.getDistancePrice());
        ride.setTotalPrice(selectedOption.getTotalPrice());
        ride.setScheduledFor(request.getScheduledFor());
        ride.setPassenger(passenger);

        Ride savedRide = rideRepository.save(ride);
        return mapToResponse(savedRide);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideResponse> getMyRides(String userEmail) {
        User passenger = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        return rideRepository.findByPassengerIdOrderByCreatedAtDesc(passenger.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RideResponse getRideById(Long rideId, String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException(RIDE_NOT_FOUND));

        boolean isPassenger = ride.getPassenger() != null
                && ride.getPassenger().getId().equals(currentUser.getId());

        boolean isAssignedDriver = ride.getDriver() != null
                && ride.getDriver().getId().equals(currentUser.getId());

        boolean canViewAvailableRideAsDriver = false;

        if (currentUser.getRole() == Role.DRIVER
                && ride.getStatus() == RideStatus.REQUESTED
                && ride.getDriver() == null) {

            DriverProfile driverProfile = driverProfileRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND));

            canViewAvailableRideAsDriver =
                    Boolean.TRUE.equals(driverProfile.getActive())
                            && driverProfile.getVehicleClass() != null
                            && driverProfile.getVehicleClass() == ride.getVehicleClass();
        }

        if (!isPassenger && !isAssignedDriver && !canViewAvailableRideAsDriver) {
            throw new AccessDeniedException("You do not have access to this ride");
        }

        return mapToResponse(ride);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideResponse> getAvailableRides(String userEmail) {
        DriverProfile driverProfile = driverProfileRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND));

        if (!Boolean.TRUE.equals(driverProfile.getActive())) {
            throw new IllegalStateException("Driver profile is not active");
        }

        if (driverProfile.getVehicleClass() == null) {
            throw new IllegalStateException("Driver vehicle class is not set");
        }

        return rideRepository
                .findByStatusAndDriverIsNullAndVehicleClassOrderByCreatedAtAsc(
                        RideStatus.REQUESTED,
                        driverProfile.getVehicleClass()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideResponse> getMyDriverRides(String userEmail) {
        User driver = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND ));

        return rideRepository.findByDriverIdOrderByCreatedAtDesc(driver.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public RideResponse acceptRide(Long rideId, String userEmail) {
        User driver = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND ));
        DriverProfile driverProfile = driverProfileRepository.findByUserId(driver.getId())
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND));
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException(RIDE_NOT_FOUND));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new IllegalStateException("Only rides with REQUESTED status can be accepted");
        }

        if (ride.getDriver() != null) {
            throw new IllegalStateException("Ride has already been accepted");
        }

        if (ride.getVehicleClass() != driverProfile.getVehicleClass()) {
            throw new IllegalStateException("Driver cannot accept ride for a different vehicle class");
        }
        ride.setDriver(driver);
        ride.setStatus(RideStatus.ACCEPTED);

        Ride savedRide = rideRepository.save(ride);
        return mapToResponse(savedRide);
    }

    @Override
    @Transactional
    public RideResponse startRide(Long rideId, String userEmail) {
        User driver = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND ));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException(RIDE_NOT_FOUND));

        validateDriverOwnership(ride, driver);

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new IllegalStateException("Only ACCEPTED rides can be started");
        }

        ride.setStatus(RideStatus.IN_PROGRESS);

        Ride savedRide = rideRepository.save(ride);
        return mapToResponse(savedRide);
    }

    @Override
    @Transactional
    public RideResponse completeRide(Long rideId, String userEmail) {
        User driver = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND ));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException(RIDE_NOT_FOUND));

        validateDriverOwnership(ride, driver);

        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only IN_PROGRESS rides can be completed");
        }

        ride.setStatus(RideStatus.COMPLETED);

        Ride savedRide = rideRepository.save(ride);
        return mapToResponse(savedRide);
    }

    @Override
    @Transactional
    public RideResponse cancelRide(Long rideId, String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND ));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException(RIDE_NOT_FOUND));

        boolean isPassenger = ride.getPassenger() != null && ride.getPassenger().getId().equals(currentUser.getId());
        boolean isAssignedDriver = ride.getDriver() != null && ride.getDriver().getId().equals(currentUser.getId());

        if (!isPassenger && !isAssignedDriver) {
            throw new AccessDeniedException("You do not have permission to cancel this ride");
        }

        if (ride.getStatus() == RideStatus.COMPLETED) {
            throw new IllegalStateException("Completed ride cannot be cancelled");
        }

        if (ride.getStatus() == RideStatus.CANCELLED) {
            throw new IllegalStateException("Ride is already cancelled");
        }

        ride.setStatus(RideStatus.CANCELLED);

        Ride savedRide = rideRepository.save(ride);
        return mapToResponse(savedRide);
    }

    private void validateDriverOwnership(Ride ride, User driver) {
        if (ride.getDriver() == null) {
            throw new IllegalStateException("Ride has no assigned driver");
        }

        if (!ride.getDriver().getId().equals(driver.getId())) {
            throw new AccessDeniedException("You are not the assigned driver for this ride");
        }
    }

    private RideResponse mapToResponse(Ride ride) {
        Long driverId = ride.getDriver() != null ? ride.getDriver().getId() : null;

        PassengerInfoResponse passengerInfo = new PassengerInfoResponse(
                ride.getPassenger().getId(),
                buildFullName(ride.getPassenger()),
                getAverageRating(ride.getPassenger().getId()),
                getTotalRatings(ride.getPassenger().getId())
        );

        DriverInfoResponse driverInfo = null;
        VehicleInfoResponse vehicleInfo = null;

        if (ride.getDriver() != null) {
            DriverProfile driverProfile = driverProfileRepository
                    .findByUserId(ride.getDriver().getId())
                    .orElse(null);

            driverInfo = new DriverInfoResponse(
                    ride.getDriver().getId(),
                    buildFullName(ride.getDriver()),
                    driverProfile != null ? driverProfile.getAverageRating() : 0.0,
                    driverProfile != null ? driverProfile.getTotalRatings() : 0
            );

            if (driverProfile != null) {
                vehicleInfo = new VehicleInfoResponse(
                        driverProfile.getVehicleClass(),
                        driverProfile.getCarBrand(),
                        driverProfile.getCarModel(),
                        driverProfile.getCarColor(),
                        driverProfile.getPlateNumber(),
                        driverProfile.getSeats()
                );
            }
        }

        return new RideResponse(
                ride.getId(),
                ride.getPickupLat(),
                ride.getPickupLng(),
                ride.getPickupAddress(),
                ride.getDropoffLat(),
                ride.getDropoffLng(),
                ride.getDropoffAddress(),
                ride.getVehicleClass(),
                ride.getStatus(),
                ride.getDistanceMeters(),
                ride.getDurationSeconds(),
                ride.getCurrency(),
                ride.getBasePrice(),
                ride.getDistancePrice(),
                ride.getTotalPrice(),
                ride.getScheduledFor(),
                ride.getPassenger().getId(),
                driverId,
                passengerInfo,
                driverInfo,
                vehicleInfo,
                ride.getCreatedAt(),
                ride.getUpdatedAt()
        );
    }

    private String buildFullName(User user) {
        String first = user.getFirstName() != null ? user.getFirstName() : "";
        String last = user.getLastName() != null ? user.getLastName() : "";
        return (first + " " + last).trim();
    }
    private Long getTotalRatings(Long userId) { return rideRatingRepository.countByToUserId(userId); }
    private Double getAverageRating(Long userId) {
        BigDecimal avg = rideRatingRepository.findAverageRatingByUserId(userId);
        return avg != null ? avg.doubleValue() : 0.0;
    }
    private void validateRideAddresses(CreateRideRequest request) {
        if (request.getPickupAddress() == null || request.getPickupAddress().isBlank()) {
            throw new IllegalStateException("Pickup address is required");
        }

        if (request.getDropoffAddress() == null || request.getDropoffAddress().isBlank()) {
            throw new IllegalStateException("Dropoff address is required");
        }
    }
}
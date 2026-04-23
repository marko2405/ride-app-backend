package com.rideapp.ride_app_backend.ride.service.implementation;

import com.rideapp.ride_app_backend.common.enums.RideStatus;
import com.rideapp.ride_app_backend.ride.dto.request.CreateRideRequest;
import com.rideapp.ride_app_backend.ride.dto.request.RideQuoteRequest;
import com.rideapp.ride_app_backend.ride.dto.response.RideOptionResponse;
import com.rideapp.ride_app_backend.ride.dto.response.RideQuoteResponse;
import com.rideapp.ride_app_backend.ride.dto.response.RideResponse;
import com.rideapp.ride_app_backend.ride.entity.Ride;
import com.rideapp.ride_app_backend.ride.repository.RideRepository;
import com.rideapp.ride_app_backend.ride.service.RideQuoteService;
import com.rideapp.ride_app_backend.ride.service.RideService;
import com.rideapp.ride_app_backend.user.entity.User;
import com.rideapp.ride_app_backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final RideQuoteService rideQuoteService;

    @Override
    @Transactional
    public RideResponse createRide(CreateRideRequest request, String userEmail) {
        User passenger = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

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
        ride.setDropoffLat(request.getDropoffLat());
        ride.setDropoffLng(request.getDropoffLng());
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
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return rideRepository.findByPassengerIdOrderByCreatedAtDesc(passenger.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RideResponse getRideById(Long rideId, String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException("Ride not found"));

        boolean isPassenger = ride.getPassenger() != null && ride.getPassenger().getId().equals(currentUser.getId());
        boolean isDriver = ride.getDriver() != null && ride.getDriver().getId().equals(currentUser.getId());

        if (!isPassenger && !isDriver) {
            throw new AccessDeniedException("You do not have access to this ride");
        }

        return mapToResponse(ride);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideResponse> getAvailableRides() {
        return rideRepository.findByStatusAndDriverIsNullOrderByCreatedAtAsc(RideStatus.REQUESTED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideResponse> getMyDriverRides(String userEmail) {
        User driver = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return rideRepository.findByDriverIdOrderByCreatedAtDesc(driver.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public RideResponse acceptRide(Long rideId, String userEmail) {
        User driver = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new IllegalStateException("Only rides with REQUESTED status can be accepted");
        }

        if (ride.getDriver() != null) {
            throw new IllegalStateException("Ride has already been accepted");
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
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException("Ride not found"));

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
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException("Ride not found"));

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
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException("Ride not found"));

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

        return new RideResponse(
                ride.getId(),
                ride.getPickupLat(),
                ride.getPickupLng(),
                ride.getDropoffLat(),
                ride.getDropoffLng(),
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
                ride.getCreatedAt(),
                ride.getUpdatedAt()
        );
    }
}
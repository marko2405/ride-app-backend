package com.rideapp.ride_app_backend.ride.service.implementation;

import com.rideapp.ride_app_backend.common.enums.RatingType;
import com.rideapp.ride_app_backend.common.enums.RideStatus;
import com.rideapp.ride_app_backend.ride.dto.request.CreateRideRatingRequest;
import com.rideapp.ride_app_backend.ride.dto.response.RideRatingResponse;
import com.rideapp.ride_app_backend.ride.dto.response.RideRatingsForRideResponse;
import com.rideapp.ride_app_backend.ride.dto.response.UserRatingSummaryResponse;
import com.rideapp.ride_app_backend.ride.entity.Ride;
import com.rideapp.ride_app_backend.ride.entity.RideRating;
import com.rideapp.ride_app_backend.ride.repository.RideRatingRepository;
import com.rideapp.ride_app_backend.ride.repository.RideRepository;
import com.rideapp.ride_app_backend.ride.service.RideRatingService;
import com.rideapp.ride_app_backend.user.entity.User;
import com.rideapp.ride_app_backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RideRatingServiceImpl implements RideRatingService {

    private final RideRatingRepository rideRatingRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RideRatingResponse createRating(Long rideId, CreateRideRatingRequest request, String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new IllegalStateException("Only completed rides can be rated");
        }

        boolean isPassenger = ride.getPassenger() != null
                && ride.getPassenger().getId().equals(currentUser.getId());

        boolean isDriver = ride.getDriver() != null
                && ride.getDriver().getId().equals(currentUser.getId());

        if (!isPassenger && !isDriver) {
            throw new AccessDeniedException("You cannot rate this ride");
        }

        RatingType ratingType;
        User toUser;

        if (isPassenger) {
            if (ride.getDriver() == null) {
                throw new IllegalStateException("Ride has no assigned driver");
            }

            ratingType = RatingType.PASSENGER_TO_DRIVER;
            toUser = ride.getDriver();
        } else {
            ratingType = RatingType.DRIVER_TO_PASSENGER;
            toUser = ride.getPassenger();
        }

        boolean alreadyExists = rideRatingRepository.existsByRideIdAndFromUserIdAndToUserIdAndRatingType(
                rideId,
                currentUser.getId(),
                toUser.getId(),
                ratingType
        );

        if (alreadyExists) {
            throw new IllegalStateException("Rating already submitted for this ride");
        }

        RideRating rideRating = new RideRating();
        rideRating.setRide(ride);
        rideRating.setFromUser(currentUser);
        rideRating.setToUser(toUser);
        rideRating.setRatingType(ratingType);
        rideRating.setScore(request.getScore());

        RideRating savedRating = rideRatingRepository.save(rideRating);

        return mapToResponse(savedRating);
    }

    @Override
    @Transactional(readOnly = true)
    public RideRatingsForRideResponse getRideRatings(Long rideId, String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException("Ride not found"));

        boolean isPassenger = ride.getPassenger() != null
                && ride.getPassenger().getId().equals(currentUser.getId());

        boolean isDriver = ride.getDriver() != null
                && ride.getDriver().getId().equals(currentUser.getId());

        if (!isPassenger && !isDriver) {
            throw new AccessDeniedException("You do not have access to ratings for this ride");
        }

        RideRatingResponse passengerToDriver = rideRatingRepository
                .findByRideIdAndRatingType(rideId, RatingType.PASSENGER_TO_DRIVER)
                .map(this::mapToResponse)
                .orElse(null);

        RideRatingResponse driverToPassenger = rideRatingRepository
                .findByRideIdAndRatingType(rideId, RatingType.DRIVER_TO_PASSENGER)
                .map(this::mapToResponse)
                .orElse(null);

        return new RideRatingsForRideResponse(passengerToDriver, driverToPassenger);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRatingSummaryResponse getUserRatingSummary(Long userId) {
        BigDecimal averageRating = rideRatingRepository.findAverageRatingByUserId(userId);
        long totalRatings = rideRatingRepository.countByToUserId(userId);

        Map<Integer, Long> breakdown = new LinkedHashMap<>();
        for (int score = 5; score >= 1; score--) {
            breakdown.put(score, rideRatingRepository.countByToUserIdAndScore(userId, score));
        }

        if (averageRating == null) {
            averageRating = BigDecimal.ZERO;
        } else {
            averageRating = averageRating.setScale(2, RoundingMode.HALF_UP);
        }

        return new UserRatingSummaryResponse(
                userId,
                averageRating,
                totalRatings,
                breakdown
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideRatingResponse> getUserRatings(Long userId) {
        return rideRatingRepository.findByToUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private RideRatingResponse mapToResponse(RideRating rideRating) {
        return new RideRatingResponse(
                rideRating.getId(),
                rideRating.getRide().getId(),
                rideRating.getFromUser().getId(),
                rideRating.getToUser().getId(),
                rideRating.getRatingType(),
                rideRating.getScore(),
                rideRating.getCreatedAt()
        );
    }
}

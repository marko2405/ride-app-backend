package com.rideapp.ride_app_backend.ride.repository;

import com.rideapp.ride_app_backend.common.enums.RatingType;
import com.rideapp.ride_app_backend.ride.entity.RideRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RideRatingRepository extends JpaRepository<RideRating, Long> {

    Optional<RideRating> findByRideIdAndRatingType(Long rideId, RatingType ratingType);

    boolean existsByRideIdAndFromUserIdAndToUserIdAndRatingType(
            Long rideId,
            Long fromUserId,
            Long toUserId,
            RatingType ratingType
    );

    List<RideRating> findByToUserIdOrderByCreatedAtDesc(Long toUserId);

    @Query("select avg(rr.score) from RideRating rr where rr.toUser.id = :userId")
    BigDecimal findAverageRatingByUserId(Long userId);

    long countByToUserId(Long userId);

    long countByToUserIdAndScore(Long userId, Integer score);
}
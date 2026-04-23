package com.rideapp.ride_app_backend.ride.entity;

import com.rideapp.ride_app_backend.common.enums.RatingType;
import com.rideapp.ride_app_backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "ride_ratings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_ride_ratings_unique",
                        columnNames = {"ride_id", "from_user_id", "to_user_id", "rating_type"}
                )
        }
)
@Getter
@Setter
public class RideRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_type", nullable = false, length = 30)
    private RatingType ratingType;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }
}

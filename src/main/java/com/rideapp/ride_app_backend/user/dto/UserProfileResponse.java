package com.rideapp.ride_app_backend.user.dto;

import com.rideapp.ride_app_backend.common.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserProfileResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private Role role;
    private Boolean enabled;
    private Instant createdAt;
}

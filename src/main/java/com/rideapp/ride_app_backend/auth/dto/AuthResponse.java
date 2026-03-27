package com.rideapp.ride_app_backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String role;
    private Boolean enabled;
}

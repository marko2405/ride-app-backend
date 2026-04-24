package com.rideapp.ride_app_backend.auth.dto;

import com.rideapp.ride_app_backend.common.enums.Role;
import com.rideapp.ride_app_backend.common.enums.VehicleClass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "First name is required.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    private String lastName;

    @Email(message = "Email is not valid.")
    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "Username is required.")
    private String username;

    @NotBlank(message = "Password is required.")
    private String password;

    @NotNull(message = "Role is required.")
    private Role role;

    private String licenseNumber;
    private Integer yearsOfExperience;
    private VehicleClass vehicleClass;
    private String carBrand;
    private String carModel;
    private String carColor;
    private String plateNumber;
    private Integer seats;
}

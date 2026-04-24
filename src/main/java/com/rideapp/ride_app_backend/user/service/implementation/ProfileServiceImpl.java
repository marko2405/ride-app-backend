package com.rideapp.ride_app_backend.user.service.implementation;

import com.rideapp.ride_app_backend.common.enums.Role;
import com.rideapp.ride_app_backend.common.exception.BadRequestException;
import com.rideapp.ride_app_backend.common.exception.NotFoundException;
import com.rideapp.ride_app_backend.user.dto.DriverProfileResponse;
import com.rideapp.ride_app_backend.user.dto.UpdateDriverProfileRequest;
import com.rideapp.ride_app_backend.user.dto.UpdateUserProfileRequest;
import com.rideapp.ride_app_backend.user.dto.UserProfileResponse;
import com.rideapp.ride_app_backend.user.entity.DriverProfile;
import com.rideapp.ride_app_backend.user.entity.User;
import com.rideapp.ride_app_backend.user.repository.DriverProfileRepository;
import com.rideapp.ride_app_backend.user.repository.UserRepository;
import com.rideapp.ride_app_backend.user.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;

    @Override
    public UserProfileResponse getMyProfile(String email) {
        User user = getUserByEmail(email);
        return mapToUserProfileResponse(user);
    }

    @Override
    public UserProfileResponse updateMyProfile(String email, UpdateUserProfileRequest request) {
        User user = getUserByEmail(email);

        boolean usernameTaken = userRepository.existsByUsername(request.getUsername())
                && !user.getUsername().equals(request.getUsername());

        if (usernameTaken) {
            throw new BadRequestException("Username is already taken.");
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());

        User savedUser = userRepository.save(user);
        return mapToUserProfileResponse(savedUser);
    }

    @Override
    public DriverProfileResponse getMyDriverProfile(String email) {
        User user = getUserByEmail(email);

        if (user.getRole() != Role.DRIVER) {
            throw new BadRequestException("Only drivers have a driver profile.");
        }

        DriverProfile driverProfile = driverProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("Driver profile not found."));

        return mapToDriverProfileResponse(user, driverProfile);
    }

    @Override
    public DriverProfileResponse updateMyDriverProfile(String email, UpdateDriverProfileRequest request) {
        User user = getUserByEmail(email);

        if (user.getRole() != Role.DRIVER) {
            throw new BadRequestException("Only drivers can update a driver profile.");
        }

        DriverProfile driverProfile = driverProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("Driver profile not found."));

        driverProfile.setLicenseNumber(request.getLicenseNumber());
        driverProfile.setYearsOfExperience(request.getYearsOfExperience());

        driverProfile.setVehicleClass(request.getVehicleClass());
        driverProfile.setCarBrand(request.getCarBrand());
        driverProfile.setCarModel(request.getCarModel());
        driverProfile.setCarColor(request.getCarColor());
        driverProfile.setPlateNumber(request.getPlateNumber());
        driverProfile.setSeats(request.getSeats());

        if (request.getActive() != null) {
            driverProfile.setActive(request.getActive());
        }

        DriverProfile savedDriverProfile = driverProfileRepository.save(driverProfile);

        return mapToDriverProfileResponse(user, savedDriverProfile);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found."));
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private DriverProfileResponse mapToDriverProfileResponse(User user, DriverProfile driverProfile) {
        return DriverProfileResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())

                .licenseNumber(driverProfile.getLicenseNumber())
                .yearsOfExperience(driverProfile.getYearsOfExperience())
                .active(driverProfile.getActive())
                .averageRating(driverProfile.getAverageRating())
                .totalRatings(driverProfile.getTotalRatings())

                .vehicleClass(driverProfile.getVehicleClass())
                .carBrand(driverProfile.getCarBrand())
                .carModel(driverProfile.getCarModel())
                .carColor(driverProfile.getCarColor())
                .plateNumber(driverProfile.getPlateNumber())
                .seats(driverProfile.getSeats())

                .build();
    }
}

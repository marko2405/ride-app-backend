package com.rideapp.ride_app_backend.admin.service.implementation;

import com.rideapp.ride_app_backend.admin.dto.AdminDashboardResponse;
import com.rideapp.ride_app_backend.admin.dto.AdminDriverResponse;
import com.rideapp.ride_app_backend.admin.dto.AdminUserResponse;
import com.rideapp.ride_app_backend.admin.service.AdminService;
import com.rideapp.ride_app_backend.common.enums.RideStatus;
import com.rideapp.ride_app_backend.common.enums.Role;
import com.rideapp.ride_app_backend.common.enums.VehicleClass;
import com.rideapp.ride_app_backend.common.exception.BadRequestException;
import com.rideapp.ride_app_backend.common.exception.NotFoundException;
import com.rideapp.ride_app_backend.ride.entity.Ride;
import com.rideapp.ride_app_backend.ride.repository.RideRepository;
import com.rideapp.ride_app_backend.user.entity.DriverProfile;
import com.rideapp.ride_app_backend.user.entity.User;
import com.rideapp.ride_app_backend.user.repository.DriverProfileRepository;
import com.rideapp.ride_app_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final RideRepository rideRepository;

    @Override
    public AdminDashboardResponse getDashboard() {
        BigDecimal totalRevenue = rideRepository.findAll()
                .stream()
                .filter(ride -> ride.getStatus() == RideStatus.COMPLETED)
                .map(Ride::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AdminDashboardResponse.builder()
                .totalUsers(userRepository.count())
                .totalPassengers(userRepository.countByRole(Role.USER))
                .totalDrivers(userRepository.countByRole(Role.DRIVER))
                .activeDrivers(driverProfileRepository.countByActive(true))

                .totalRides(rideRepository.count())
                .requestedRides(rideRepository.countByStatus(RideStatus.REQUESTED))
                .acceptedRides(rideRepository.countByStatus(RideStatus.ACCEPTED))
                .inProgressRides(rideRepository.countByStatus(RideStatus.IN_PROGRESS))
                .completedRides(rideRepository.countByStatus(RideStatus.COMPLETED))
                .cancelledRides(rideRepository.countByStatus(RideStatus.CANCELLED))

                .economicRides(rideRepository.countByVehicleClass(VehicleClass.ECONOMIC))
                .businessRides(rideRepository.countByVehicleClass(VehicleClass.BUSINESS))
                .comfortRides(rideRepository.countByVehicleClass(VehicleClass.COMFORT))

                .totalRevenue(totalRevenue)
                .build();
    }

    @Override
    public List<AdminUserResponse> getUsers() {
        return userRepository.findByRoleOrderByCreatedAtDesc(Role.USER)
                .stream()
                .map(this::mapToAdminUserResponse)
                .toList();
    }

    @Override
    public List<AdminDriverResponse> getDrivers() {
        return driverProfileRepository.findAllWithUser()
                .stream()
                .map(this::mapToAdminDriverResponse)
                .toList();
    }

    @Override
    public void deleteUser(Long userId) {
        User user = getUserById(userId);

        if (user.getRole() != Role.USER) {
            throw new BadRequestException("Only passengers can be deleted from this endpoint.");
        }

        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void deleteDriver(Long userId) {
        User user = getUserById(userId);

        if (user.getRole() != Role.DRIVER) {
            throw new BadRequestException("Only drivers can be deleted from this endpoint.");
        }

        DriverProfile driverProfile = driverProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Driver profile not found."));

        driverProfile.setActive(false);
        driverProfileRepository.save(driverProfile);

        user.setEnabled(false);
        userRepository.save(user);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));
    }

    private AdminUserResponse mapToAdminUserResponse(User user) {
        return AdminUserResponse.builder()
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

    private AdminDriverResponse mapToAdminDriverResponse(DriverProfile driverProfile) {
        User user = driverProfile.getUser();

        return AdminDriverResponse.builder()
                .userId(user.getId())
                .driverProfileId(driverProfile.getId())

                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())

                .enabled(user.getEnabled())
                .active(driverProfile.getActive())

                .vehicleClass(driverProfile.getVehicleClass())
                .licenseNumber(driverProfile.getLicenseNumber())
                .yearsOfExperience(driverProfile.getYearsOfExperience())

                .averageRating(driverProfile.getAverageRating())
                .totalRatings(driverProfile.getTotalRatings())

                .carBrand(driverProfile.getCarBrand())
                .carModel(driverProfile.getCarModel())
                .carColor(driverProfile.getCarColor())
                .plateNumber(driverProfile.getPlateNumber())
                .seats(driverProfile.getSeats())
                .build();
    }
    @Override
    public void toggleUserStatus(Long userId) {
        User user = getUserById(userId);

        if (user.getRole() != Role.USER) {
            throw new BadRequestException("Only passengers allowed.");
        }

        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
    }
    @Override
    public void toggleDriverStatus(Long userId) {
        User user = getUserById(userId);

        if (user.getRole() != Role.DRIVER) {
            throw new BadRequestException("Only drivers allowed.");
        }

        DriverProfile driverProfile = driverProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Driver profile not found."));

        boolean newStatus = !user.getEnabled();

        user.setEnabled(newStatus);
        driverProfile.setActive(newStatus);

        userRepository.save(user);
        driverProfileRepository.save(driverProfile);
    }
}

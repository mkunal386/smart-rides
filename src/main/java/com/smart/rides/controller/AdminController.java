package com.smart.rides.controller;

import com.smart.rides.dto.AdminOverviewStats;
import com.smart.rides.entity.RideStatus;
import com.smart.rides.entity.BookingStatus;
import com.smart.rides.entity.PaymentStatus;
// import com.smart.rides.entity.Role;
import com.smart.rides.entity.Booking;
import com.smart.rides.entity.Ride;
import com.smart.rides.repository.UserRepository;
import com.smart.rides.entity.User;
import com.smart.rides.entity.UserStatus;
import com.smart.rides.repository.RideRepository;
import com.smart.rides.repository.BookingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
// import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000"})
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private static final double COMMISSION_RATE = 0.09; // 9% commission

    @GetMapping("/overview/stats")
    public ResponseEntity<AdminOverviewStats> getOverviewStats() {
        AdminOverviewStats stats = new AdminOverviewStats();

        // --- Change: Corrected logic for User Stats based on activity ---
        stats.setTotalUsers(userRepository.count());
        List<Ride> allRides = rideRepository.findAll();
        List<Booking> allBookings = bookingRepository.findAll();

        long totalDrivers = allRides.stream()
                .map(ride -> ride.getDriver().getId())
                .distinct()
                .count();

        long totalPassengers = allBookings.stream()
                .map(booking -> booking.getPassenger().getId())
                .distinct()
                .count();

        stats.setTotalDrivers(totalDrivers);
        stats.setTotalPassengers(totalPassengers);

        // Ride Stats
        stats.setTotalRides(rideRepository.count());
        stats.setActiveRides(rideRepository.countByStatus(RideStatus.ACTIVE));
        stats.setCompletedRides(rideRepository.countByStatus(RideStatus.COMPLETED));
        stats.setCancelledRides(rideRepository.countByStatus(RideStatus.CANCELLED));

        // Booking Stats
        stats.setTotalBookings(bookingRepository.count());
        stats.setPendingBookings(bookingRepository.countByStatus(BookingStatus.PENDING));
        stats.setApprovedBookings(bookingRepository.countByStatus(BookingStatus.DRIVER_ACCEPTED));
        stats.setRejectedBookings(bookingRepository.countByStatus(BookingStatus.DRIVER_REJECTED));

        // Earnings Summary (Calculated from Completed Bookings)
        List<Booking> allCompletedBookings = bookingRepository.findAll().stream()
                .filter(booking -> booking.getPaymentStatus() == PaymentStatus.COMPLETED)
                .toList();

        double totalEarnings = allCompletedBookings.stream()
                .mapToDouble(Booking::getFare)
                .sum();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime startOfDay = now.truncatedTo(ChronoUnit.DAYS);

        double monthlyEarnings = allCompletedBookings.stream()
                .filter(booking -> booking.getBookingTime().isAfter(startOfMonth))
                .mapToDouble(Booking::getFare)
                .sum();

        double dailyEarnings = allCompletedBookings.stream()
                .filter(booking -> booking.getBookingTime().isAfter(startOfDay))
                .mapToDouble(Booking::getFare)
                .sum();

        // Calculate platform's commission based on the earnings
        stats.setTotalEarnings(totalEarnings * COMMISSION_RATE);
        stats.setMonthlyEarnings(monthlyEarnings * COMMISSION_RATE);
        stats.setDailyEarnings(dailyEarnings * COMMISSION_RATE);

        return ResponseEntity.ok(stats);
    }

    // --- Users management endpoints ---
    @GetMapping("/users")
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PatchMapping("/users/{id}/block")
    public ResponseEntity<User> blockUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setIsBlocked(true);
                    user.setStatus(UserStatus.BLOCKED);
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/users/{id}/unblock")
    public ResponseEntity<User> unblockUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setIsBlocked(false);
                    if (user.getIsVerified()) {
                        user.setStatus(UserStatus.VERIFIED);
                    } else {
                        user.setStatus(UserStatus.ACTIVE);
                    }
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/users/{id}/verify")
    public ResponseEntity<User> verifyUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setIsVerified(true);
                    user.setStatus(UserStatus.VERIFIED);
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Rides management endpoints ---
    @GetMapping("/rides")
    public ResponseEntity<List<Ride>> listRides() {
        return ResponseEntity.ok(rideRepository.findAll());
    }

    // --- Bookings management endpoints ---
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> listBookings() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }
}
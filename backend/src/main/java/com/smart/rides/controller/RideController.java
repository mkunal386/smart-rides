package com.smart.rides.controller;

import com.smart.rides.entity.Ride;
import com.smart.rides.entity.User;
import com.smart.rides.repository.RideRepository;
import com.smart.rides.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/rides")
@CrossOrigin(origins = {"http://localhost:3000"})
public class RideController {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public RideController(RideRepository rideRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    // DRIVER: Host a ride
    @PostMapping("/host")
    public ResponseEntity<?> hostRide(
            @RequestParam Long driverId,
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam String date,     // yyyy-MM-dd
            @RequestParam String time,     // HH:mm
            @RequestParam int seats,
            @RequestParam double fare
    ) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        LocalDate d = LocalDate.parse(date);
        LocalTime t = LocalTime.parse(time);
        LocalDateTime dateTime = LocalDateTime.of(d, t);

        // Restrict past rides
        if (dateTime.isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Cannot host ride in the past!");
        }

        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setSource(source);
        ride.setDestination(destination);
        ride.setDateTime(dateTime);
        ride.setAvailableSeats(seats);
        ride.setFare(fare);

        Ride saved = rideRepository.save(ride);
        return ResponseEntity.ok(saved);
    }

    // PASSENGER: Search rides
    @GetMapping("/search")
    public ResponseEntity<List<Ride>> searchRides(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam(required = false) String date,   // optional
            @RequestParam(required = false) String startTime, // optional (HH:mm)
            @RequestParam(required = false) String endTime    // optional (HH:mm)
    ) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        LocalDate startDate = (date != null) ? LocalDate.parse(date) : today;
        LocalDate endDate = (date != null) ? LocalDate.parse(date) : tomorrow;

        LocalTime startT = (startTime != null) ? LocalTime.parse(startTime) : LocalTime.MIN;
        LocalTime endT = (endTime != null) ? LocalTime.parse(endTime) : LocalTime.MAX;

        LocalDateTime start = LocalDateTime.of(startDate, startT);
        LocalDateTime end = LocalDateTime.of(endDate, endT);

        // Always only upcoming rides
        if (start.isBefore(LocalDateTime.now())) {
            start = LocalDateTime.now();
        }

        List<Ride> rides = rideRepository.findBySourceIgnoreCaseAndDestinationIgnoreCaseAndDateTimeBetween(source, destination, start, end);


        return ResponseEntity.ok(rides);
    }
}

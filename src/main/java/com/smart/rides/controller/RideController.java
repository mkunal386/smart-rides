package com.smart.rides.controller;

import com.smart.rides.entity.Ride;
import com.smart.rides.entity.User;
import com.smart.rides.entity.RideStatus; // Change: New import for RideStatus
import com.smart.rides.entity.Role; // Change: New import for Role
import com.smart.rides.repository.RideRepository;
import com.smart.rides.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public RideController(RideRepository rideRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/host")
    public ResponseEntity<?> hostRide(@RequestBody Ride rideDetails) {
        User driver = userRepository.findById(rideDetails.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found with ID: " + rideDetails.getDriverId()));

        Ride newRide = new Ride();
        newRide.setDriver(driver);
        newRide.setSource(rideDetails.getSource());
        newRide.setDestination(rideDetails.getDestination());
        newRide.setDateTime(rideDetails.getDateTime());
        newRide.setAvailableSeats(rideDetails.getAvailableSeats());
        newRide.setFare(rideDetails.getFare());
        newRide.setCarModel(rideDetails.getCarModel());
        newRide.setPlateNumber(rideDetails.getPlateNumber());
        newRide.setPhoneNumber(rideDetails.getPhoneNumber());
        newRide.setCarColor(rideDetails.getCarColor());
        newRide.setStatus(RideStatus.ACTIVE); // Change: Set the initial status

        Ride savedRide = rideRepository.save(newRide);
        return ResponseEntity.ok(savedRide);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Ride>> searchRides(
            @RequestParam String source,
            @RequestParam String destination) {

        List<Ride> rides = rideRepository.findBySourceContainingIgnoreCaseAndDestinationContainingIgnoreCaseAndDateTimeAfter(
                source, destination, LocalDateTime.now());
        return ResponseEntity.ok(rides);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Ride>> getRidesByDriver(@PathVariable Long driverId) {
        List<Ride> hostedRides = rideRepository.findByDriver_Id(driverId);
        return ResponseEntity.ok(hostedRides);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/admin/all")
    public ResponseEntity<List<Ride>> getAllRidesForAdmin() {
        List<Ride> allRides = rideRepository.findAll();
        return ResponseEntity.ok(allRides);
    }

    // --- Change: New endpoint to end a ride and update driver role ---
    @PostMapping("/end/{rideId}")
    public ResponseEntity<String> endRide(@PathVariable Long rideId) {
        return rideRepository.findById(rideId)
                .map(ride -> {
                    // Set the ride status to COMPLETED
                    ride.setStatus(RideStatus.COMPLETED);
                    rideRepository.save(ride);

                    User driver = ride.getDriver();
                    // Count any other active rides for this driver
                    long otherActiveRides = rideRepository.countByDriverAndStatus(driver, RideStatus.ACTIVE);

                    // If this was the driver's last active ride, change their role back to PASSENGER
                    if (otherActiveRides == 0) {
                        driver.setRole(Role.PASSENGER);
                        userRepository.save(driver);
                    }

                    return ResponseEntity.ok("Ride ended successfully. Driver role updated if necessary.");
                })
                .orElseGet(() -> ResponseEntity.status(404).body("Ride not found."));
    }
}
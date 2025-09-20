package com.smart.rides.controller;

import com.smart.rides.entity.Ride;
import com.smart.rides.entity.User;
import com.smart.rides.repository.RideRepository;
import com.smart.rides.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

// Add the @CrossOrigin annotation to the whole class
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
    
    
 // --- ADD THIS ENTIRE METHOD ---
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Ride>> getRidesByDriver(@PathVariable Long driverId) {
        // This calls the findByDriver_Id method you already have in your repository
        List<Ride> hostedRides = rideRepository.findByDriver_Id(driverId);
        return ResponseEntity.ok(hostedRides);
    }
}


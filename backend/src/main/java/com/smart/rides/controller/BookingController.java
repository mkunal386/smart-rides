// src/main/java/com/smart/rides/controller/BookingController.java
package com.smart.rides.controller;

import com.smart.rides.entity.Booking;
import com.smart.rides.entity.Ride;
import com.smart.rides.entity.User;
import com.smart.rides.repository.BookingRepository;
import com.smart.rides.repository.RideRepository;
import com.smart.rides.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = {"http://localhost:3000"})
public class BookingController {

    private final BookingRepository bookingRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public BookingController(BookingRepository bookingRepository, RideRepository rideRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(
            @RequestParam Long passengerId,
            @RequestParam Long rideId,
            @RequestParam int seats
    ) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        User passenger = userRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Passenger not found"));

        if (ride.getAvailableSeats() < seats) {
            return ResponseEntity.badRequest().body("Not enough seats available!");
        }

        // reduce seats
        ride.setAvailableSeats(ride.getAvailableSeats() - seats);
        rideRepository.save(ride);

        Booking booking = new Booking(ride, passenger, seats);
        Booking savedBooking = bookingRepository.save(booking);

        return ResponseEntity.ok(savedBooking);
    }
}

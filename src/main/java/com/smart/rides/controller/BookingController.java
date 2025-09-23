package com.smart.rides.controller;

import com.smart.rides.dto.BookingRequest;
import com.smart.rides.dto.DriverResponse;
import com.smart.rides.dto.PaymentConfirmation;
import com.smart.rides.entity.Booking;
import com.smart.rides.entity.BookingStatus;
import com.smart.rides.entity.Ride;
import com.smart.rides.entity.User;
import com.smart.rides.entity.Transaction;
import com.smart.rides.repository.BookingRepository;
import com.smart.rides.repository.RideRepository;
import com.smart.rides.repository.TransactionRepository;
import com.smart.rides.repository.UserRepository;
import com.smart.rides.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.thymeleaf.context.Context;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final EmailService emailService;

    public BookingController(BookingRepository b, RideRepository r, UserRepository u, TransactionRepository t, EmailService e) {
        this.bookingRepository = b;
        this.rideRepository = r;
        this.userRepository = u;
        this.transactionRepository = t;
        this.emailService = e;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest payload) {
        Ride ride = rideRepository.findById(payload.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        User passenger = userRepository.findById(payload.getPassengerId())
                .orElseThrow(() -> new RuntimeException("Passenger not found"));

        if (ride.getDriver().getId().equals(passenger.getId())) {
            return ResponseEntity.badRequest().body("You cannot book your own ride!");
        }

        int seatsToBook = 1;
        if (ride.getAvailableSeats() < seatsToBook) {
            return ResponseEntity.badRequest().body("Not enough seats available!");
        }

        Booking booking = new Booking();
        booking.setRide(ride);
        booking.setPassenger(passenger);
        booking.setSeatsBooked(seatsToBook);
        booking.setFare(payload.getFare());
        booking.setStatus(BookingStatus.PENDING);
        Booking savedBooking = bookingRepository.save(booking);

        User driver = ride.getDriver();
        String driverSubject = "New Ride Booking!";

        Context driverContext = new Context();
        driverContext.setVariable("driverName", driver.getName());
        driverContext.setVariable("pickupLocation", ride.getSource());
        driverContext.setVariable("destination", ride.getDestination());
        driverContext.setVariable("rideDate", ride.getDateTime().toLocalDate().toString());
        driverContext.setVariable("rideTime", ride.getDateTime().toLocalTime().toString());
        driverContext.setVariable("fare", savedBooking.getFare());
        driverContext.setVariable("passengerName", passenger.getName());
        driverContext.setVariable("passengerPhone", passenger.getPhone());
        driverContext.setVariable("bookingId", savedBooking.getId());

        emailService.sendHtmlEmail(driver.getEmail(), driverSubject, "driver-notification", driverContext);

        String passengerSubject = "Ride Request Submitted!";

        Context passengerContext = new Context();
        passengerContext.setVariable("passengerName", passenger.getName());
        passengerContext.setVariable("rideOrigin", ride.getSource());
        passengerContext.setVariable("rideDestination", ride.getDestination());
        passengerContext.setVariable("driverName", driver.getName());
        passengerContext.setVariable("departureTime", ride.getDateTime());

        emailService.sendHtmlEmail(passenger.getEmail(), passengerSubject, "ride-request-submitted", passengerContext);

        return ResponseEntity.ok(savedBooking);
    }

    @PostMapping("/driver-response")
    public ResponseEntity<?> handleDriverResponse(@RequestBody DriverResponse payload) {
        Booking booking = bookingRepository.findById(payload.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        User driver = booking.getRide().getDriver();
        User passenger = booking.getPassenger();

        if (!driver.getId().equals(payload.getDriverId())) {
            return ResponseEntity.badRequest().body("Unauthorized driver action.");
        }

        String decision = payload.getDecision();

        if ("accept".equalsIgnoreCase(decision)) {
            booking.setStatus(BookingStatus.DRIVER_ACCEPTED);
            Ride ride = booking.getRide();
            if (ride.getAvailableSeats() >= booking.getSeatsBooked()) {
                ride.setAvailableSeats(ride.getAvailableSeats() - booking.getSeatsBooked());
                rideRepository.save(ride);
            } else {
                return ResponseEntity.badRequest().body("Not enough seats available on the ride.");
            }

            emailService.sendRideAcceptedEmail(
                    passenger.getEmail(),
                    passenger.getName(),
                    driver.getName(),
                    booking.getRide().getSource(),
                    booking.getRide().getDestination(),
                    booking.getRide().getDateTime()
            );

        } else if ("reject".equalsIgnoreCase(decision)) {
            booking.setStatus(BookingStatus.DRIVER_REJECTED);

            emailService.sendRideRejectedEmail(
                    passenger.getEmail(),
                    passenger.getName(),
                    driver.getName(),
                    booking.getRide().getSource(),
                    booking.getRide().getDestination()
            );
        } else {
            return ResponseEntity.badRequest().body("Invalid decision. Must be 'accept' or 'reject'.");
        }

        bookingRepository.save(booking);
        return ResponseEntity.ok("Booking status updated successfully.");
    }

    // --- New API Endpoint for Payment Confirmation ---
    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmation payload) {
        Booking booking = bookingRepository.findById(payload.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        if (booking.getStatus() != BookingStatus.DRIVER_ACCEPTED) {
            return ResponseEntity.badRequest().body("This booking is not ready for payment.");
        }

        // Update booking status to CONFIRMED
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking savedBooking = bookingRepository.save(booking);

        // Create a new Transaction record
        Transaction transaction = new Transaction();
        transaction.setUserId(savedBooking.getPassenger().getId());
        transaction.setBooking(savedBooking);
        transaction.setAmount(savedBooking.getFare());
        transaction.setSeatsBooked(savedBooking.getSeatsBooked());
        transaction.setStatus("COMPLETED");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setPaymentMethod("Card"); // Assuming 'Card' for now
        transaction.setTransactionId(payload.getTransactionId());
        transactionRepository.save(transaction);

        // Send final confirmation emails to both passenger and driver
        emailService.sendFinalConfirmationEmail(
                savedBooking.getPassenger().getEmail(),
                savedBooking.getPassenger().getName(),
                savedBooking.getRide().getDriver().getName(),
                savedBooking.getRide().getSource(),
                savedBooking.getRide().getDestination(),
                savedBooking.getRide().getDateTime(),
                savedBooking.getFare()
        );

        emailService.sendDriverFinalNotification(
                savedBooking.getRide().getDriver().getEmail(),
                savedBooking.getRide().getDriver().getName(),
                savedBooking.getPassenger().getName(),
                savedBooking.getRide().getSource(),
                savedBooking.getRide().getDestination(),
                savedBooking.getFare()
        );

        return ResponseEntity.ok("Payment confirmed and booking finalized.");
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/driver-requests/{driverId}")
    public ResponseEntity<List<Booking>> getPendingBookingsForDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(bookingRepository.findByRide_Driver_IdAndStatus(driverId, BookingStatus.PENDING));
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<Booking>> getBookingsByPassenger(@PathVariable Long passengerId) {
        return ResponseEntity.ok(bookingRepository.findByPassenger_Id(passengerId));
    }

    // --- New API Endpoint for Admin Dashboard ---
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/admin/all")
    public ResponseEntity<List<Booking>> getAllBookingsForAdmin() {
        List<Booking> allBookings = bookingRepository.findAll();
        return ResponseEntity.ok(allBookings);
    }
}
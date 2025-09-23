package com.smart.rides.service;

import com.smart.rides.entity.*;
import com.smart.rides.repository.BookingRepository;
import com.smart.rides.repository.CancellationRepository;
import com.smart.rides.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class CancellationService {

    private final CancellationRepository cancellationRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public CancellationService(CancellationRepository cancellationRepository,
                               BookingRepository bookingRepository,
                               UserRepository userRepository) {
        this.cancellationRepository = cancellationRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public java.util.List<Cancellation> findAll() {
        return cancellationRepository.findAll();
    }

    public Cancellation cancelBooking(Long bookingId, Long userId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        double fee = calculateCancellationFee(booking);

        Cancellation cancellation = new Cancellation();
        cancellation.setBooking(booking);
        cancellation.setUser(user);
        cancellation.setReason(reason);
        cancellation.setFeeApplied(fee);
        cancellation.setStatus(fee > 0 ? CancellationStatus.FEE_APPLIED : CancellationStatus.COMPLETED);

        // Update booking/ride status if needed
        booking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);

        return cancellationRepository.save(cancellation);
    }

    private double calculateCancellationFee(Booking booking) {
        LocalDateTime rideTime = booking.getRide().getDateTime();
        long hoursUntilRide = Duration.between(LocalDateTime.now(), rideTime).toHours();
        double baseFare = booking.getFare();
        if (hoursUntilRide >= 24) {
            return 0;
        } else if (hoursUntilRide >= 6) {
            return Math.round(baseFare * 0.10 * 100.0) / 100.0; // 10%
        } else if (hoursUntilRide >= 2) {
            return Math.round(baseFare * 0.25 * 100.0) / 100.0; // 25%
        } else {
            return Math.round(baseFare * 0.50 * 100.0) / 100.0; // 50%
        }
    }
}



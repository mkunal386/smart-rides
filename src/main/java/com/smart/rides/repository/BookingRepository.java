package com.smart.rides.repository;

import com.smart.rides.entity.Booking;
import com.smart.rides.entity.BookingStatus; // New import for the enum
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Find all bookings for a given passenger
    // List<Booking> findByPassengerId(Long passengerId);
    List<Booking> findByPassenger_Id(Long passengerId);

    // Add this new method to your BookingRepository interface
    List<Booking> findByRide_Driver_IdAndStatus(Long driverId, BookingStatus status);
}
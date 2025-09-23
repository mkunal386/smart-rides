package com.smart.rides.repository;

import com.smart.rides.entity.Booking;
import com.smart.rides.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Find all bookings for a given passenger
    List<Booking> findByPassenger_Id(Long passengerId);

    // Find bookings for a driver by status
    List<Booking> findByRide_Driver_IdAndStatus(Long driverId, BookingStatus status);

    // --- Change: New method for Admin Dashboard ---
    // Counts the total number of bookings for a specific status
    long countByStatus(BookingStatus status);
}
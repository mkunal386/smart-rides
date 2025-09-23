package com.smart.rides.repository;

import com.smart.rides.entity.Ride;
import com.smart.rides.entity.RideStatus;
import com.smart.rides.entity.User; // Change: New import for User
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findBySourceContainingIgnoreCaseAndDestinationContainingIgnoreCaseAndDateTimeAfter(
            String source,
            String destination,
            LocalDateTime dateTime
    );

    List<Ride> findByDriver_Id(Long driverId);

    long countByStatus(RideStatus status);

    List<Ride> findAll();

    // --- Change: New method to count active rides for a specific driver ---
    long countByDriverAndStatus(User driver, RideStatus status);
}
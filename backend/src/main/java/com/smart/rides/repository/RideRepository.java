package com.smart.rides.repository;

import com.smart.rides.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    // Search rides by source, destination, and time window (case-insensitive)
    List<Ride> findBySourceIgnoreCaseAndDestinationIgnoreCaseAndDateTimeBetween(
            String source,
            String destination,
            LocalDateTime start,
            LocalDateTime end
    );

    // Get only upcoming rides (dateTime >= now)
    List<Ride> findByDateTimeAfter(LocalDateTime now);
}

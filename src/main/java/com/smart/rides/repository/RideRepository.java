package com.smart.rides.repository;

import com.smart.rides.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    // This is the single, correct method that your RideController is trying to call.
    // It replaces the two older methods.
    List<Ride> findBySourceContainingIgnoreCaseAndDestinationContainingIgnoreCaseAndDateTimeAfter(
        String source,
        String destination,
        LocalDateTime dateTime
    );
    
    
    //List<Ride> findByDriverId(Long driverId);
 // Corrected version
    List<Ride> findByDriver_Id(Long driverId);
}


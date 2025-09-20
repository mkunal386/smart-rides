// src/main/java/com/smart/rides/repository/BookingRepository.java
package com.smart.rides.repository;

import com.smart.rides.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}

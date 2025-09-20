package com.smart.rides.dto;

// This class is a Data Transfer Object (DTO) used to carry data from the frontend
// when a user wants to create a booking.
public class BookingRequest {
    private Long passengerId;
    private Long rideId;
    private double fare;

    // Getters and setters are required for Spring Boot to map the JSON data
    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }
}

package com.smart.rides.dto;

public class DriverResponse {
    private Long bookingId;
    private Long driverId;
    private String decision; // "accept" or "reject"

    // Getters and Setters
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
}
package com.smart.rides.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger; // This field must be named 'passenger'

    private int seatsBooked;
    private double fare;

    @Column(updatable = false)
    private LocalDateTime bookingTime;

    // --- Change: New field for booking status ---
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    // --- Change: New field for payment status ---
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @PrePersist
    protected void onCreate() {
        if (bookingTime == null) {
            bookingTime = LocalDateTime.now();
        }
        // --- Change: Set default payment status to PENDING ---
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.PENDING;
        }
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }
    public User getPassenger() { return passenger; }
    public void setPassenger(User passenger) { this.passenger = passenger; }
    public int getSeatsBooked() { return seatsBooked; }
    public void setSeatsBooked(int seatsBooked) { this.seatsBooked = seatsBooked; }
    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }
    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    // --- New getter and setter for status ---
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    // --- Change: Getter and Setter for paymentStatus ---
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
}
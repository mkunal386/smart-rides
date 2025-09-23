package com.smart.rides.dto;

import com.smart.rides.entity.Role;

public class AdminOverviewStats {
    private long totalUsers;
    private long totalDrivers;
    private long totalPassengers;
    private long totalRides;
    private long activeRides;
    private long completedRides;
    private long cancelledRides;
    private long totalBookings;
    private long pendingBookings;
    private long approvedBookings;
    private long rejectedBookings;
    private double totalEarnings;
    private double monthlyEarnings;
    private double dailyEarnings;

    // Getters and Setters
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalDrivers() { return totalDrivers; }
    public void setTotalDrivers(long totalDrivers) { this.totalDrivers = totalDrivers; }
    public long getTotalPassengers() { return totalPassengers; }
    public void setTotalPassengers(long totalPassengers) { this.totalPassengers = totalPassengers; }
    public long getTotalRides() { return totalRides; }
    public void setTotalRides(long totalRides) { this.totalRides = totalRides; }
    public long getActiveRides() { return activeRides; }
    public void setActiveRides(long activeRides) { this.activeRides = activeRides; }
    public long getCompletedRides() { return completedRides; }
    public void setCompletedRides(long completedRides) { this.completedRides = completedRides; }
    public long getCancelledRides() { return cancelledRides; }
    public void setCancelledRides(long cancelledRides) { this.cancelledRides = cancelledRides; }
    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }
    public long getPendingBookings() { return pendingBookings; }
    public void setPendingBookings(long pendingBookings) { this.pendingBookings = pendingBookings; }
    public long getApprovedBookings() { return approvedBookings; }
    public void setApprovedBookings(long approvedBookings) { this.approvedBookings = approvedBookings; }
    public long getRejectedBookings() { return rejectedBookings; }
    public void setRejectedBookings(long rejectedBookings) { this.rejectedBookings = rejectedBookings; }
    public double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(double totalEarnings) { this.totalEarnings = totalEarnings; }
    public double getMonthlyEarnings() { return monthlyEarnings; }
    public void setMonthlyEarnings(double monthlyEarnings) { this.monthlyEarnings = monthlyEarnings; }
    public double getDailyEarnings() { return dailyEarnings; }
    public void setDailyEarnings(double dailyEarnings) { this.dailyEarnings = dailyEarnings; }
}
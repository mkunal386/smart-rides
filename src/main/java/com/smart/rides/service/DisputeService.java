package com.smart.rides.service;

import com.smart.rides.entity.*;
import com.smart.rides.repository.BookingRepository;
import com.smart.rides.repository.DisputeRepository;
import com.smart.rides.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public DisputeService(DisputeRepository disputeRepository,
                          BookingRepository bookingRepository,
                          UserRepository userRepository) {
        this.disputeRepository = disputeRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public java.util.List<Dispute> findAll() {
        return disputeRepository.findAll();
    }

    public Dispute openDispute(Long bookingId, Long userId, String type, String description) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Dispute dispute = new Dispute();
        dispute.setBooking(booking);
        dispute.setRaisedBy(user);
        dispute.setType(type);
        dispute.setDescription(description);
        dispute.setStatus(DisputeStatus.OPEN);
        return disputeRepository.save(dispute);
    }

    public Dispute resolveDispute(Long disputeId, String resolutionNote, Double refundAmount, Double penaltyAmount, boolean rejected) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new IllegalArgumentException("Dispute not found"));
        dispute.setAdminResolutionNote(resolutionNote);
        dispute.setRefundAmount(refundAmount);
        dispute.setPenaltyAmount(penaltyAmount);
        dispute.setStatus(rejected ? DisputeStatus.REJECTED : DisputeStatus.RESOLVED);
        return disputeRepository.save(dispute);
    }
}



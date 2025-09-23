package com.smart.rides.controller;

import com.smart.rides.entity.Dispute;
import com.smart.rides.service.DisputeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/disputes")
@CrossOrigin(origins = {"http://localhost:3000"})
public class DisputeController {

    private final DisputeService disputeService;

    public DisputeController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    public static class OpenDisputeRequest {
        public Long bookingId;
        public Long userId;
        public String type;
        public String description;
    }

    public static class ResolveDisputeRequest {
        public String resolutionNote;
        public Double refundAmount;
        public Double penaltyAmount;
        public boolean rejected;
    }

    @PostMapping("/open")
    public ResponseEntity<Dispute> open(@RequestBody OpenDisputeRequest request) {
        Dispute dispute = disputeService.openDispute(request.bookingId, request.userId, request.type, request.description);
        return ResponseEntity.ok(dispute);
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<Dispute> resolve(@PathVariable Long id, @RequestBody ResolveDisputeRequest request) {
        Dispute dispute = disputeService.resolveDispute(id, request.resolutionNote, request.refundAmount, request.penaltyAmount, request.rejected);
        return ResponseEntity.ok(dispute);
    }

    @GetMapping
    public ResponseEntity<List<Dispute>> listAll() {
        return ResponseEntity.ok(disputeService.findAll());
    }
}



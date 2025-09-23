package com.smart.rides.controller;

import com.smart.rides.entity.Cancellation;
import com.smart.rides.service.CancellationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cancellations")
@CrossOrigin(origins = {"http://localhost:3000"})
public class CancellationController {

    private final CancellationService cancellationService;

    public CancellationController(CancellationService cancellationService) {
        this.cancellationService = cancellationService;
    }

    public static class CancelRequest {
        public Long bookingId;
        public Long userId;
        public String reason;
    }

    @PostMapping("/create")
    public ResponseEntity<Cancellation> cancel(@RequestBody CancelRequest request) {
        Cancellation cancellation = cancellationService.cancelBooking(request.bookingId, request.userId, request.reason);
        return ResponseEntity.ok(cancellation);
    }

    @GetMapping
    public ResponseEntity<List<Cancellation>> listAll() {
        return ResponseEntity.ok(cancellationService.findAll());
    }
}



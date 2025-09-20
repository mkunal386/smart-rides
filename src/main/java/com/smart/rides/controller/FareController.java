package com.smart.rides.controller;

import com.smart.rides.service.FareService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/fare")
// --- THIS ANNOTATION IS THE FIX ---
@CrossOrigin(origins = {"http://localhost:3000"})
public class FareController {

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculateFare(@RequestBody Map<String, Double> payload) {
        try {
            double startLat = payload.get("startLat");
            double startLon = payload.get("startLon");
            double endLat = payload.get("endLat");
            double endLon = payload.get("endLon");

            Map<String, Object> fareDetails = fareService.calculateFare(startLat, startLon, endLat, endLon);
            return ResponseEntity.ok(fareDetails);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); // Log the exception
            return ResponseEntity.status(500).body(Map.of("error", "Failed to calculate distance", "message", e.getMessage()));
        }
    }
}

package com.smart.rides.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class FareService {

    private static final double BASE_FARE = 50.0; // Base fare in local currency
    private static final double RATE_PER_KM = 12.0; // Rate per kilometer

    // The method signature is updated to declare the exceptions it can throw
    public Map<String, Object> calculateFare(double startLat, double startLon, double endLat, double endLon)
            throws IOException, InterruptedException {

        // 1. Get distance from OpenStreetMap OSRM API
        String osrmUrl = String.format(
                "http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=false",
                startLon, startLat, endLon, endLat
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(osrmUrl))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to get distance from OSRM API: " + response.body());
        }

        // 2. Parse the distance from the JSON response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());
        JsonNode route = root.path("routes").get(0);
        double distanceMeters = route.path("distance").asDouble();
        double distanceKm = distanceMeters / 1000.0;

        // 3. Calculate the fare
        double fare = BASE_FARE + (distanceKm * RATE_PER_KM);

        // 4. Return all details
        Map<String, Object> fareDetails = new HashMap<>();
        fareDetails.put("distanceKm", Math.round(distanceKm * 100.0) / 100.0); // Round to 2 decimal places
        fareDetails.put("baseFare", BASE_FARE);
        fareDetails.put("ratePerKm", RATE_PER_KM);
        fareDetails.put("fare", Math.round(fare * 100.0) / 100.0);

        return fareDetails;
    }
}

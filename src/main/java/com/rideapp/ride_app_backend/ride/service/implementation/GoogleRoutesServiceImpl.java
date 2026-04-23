package com.rideapp.ride_app_backend.ride.service.implementation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rideapp.ride_app_backend.ride.dto.response.RouteInfoResponse;
import com.rideapp.ride_app_backend.ride.service.GoogleRoutesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleRoutesServiceImpl implements GoogleRoutesService {

    @Value("${google.maps.api-key}")
    private String googleMapsApiKey;

    @Value("${google.routes.url}")
    private String googleRoutesUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public RouteInfoResponse getRouteInfo(Double pickupLat, Double pickupLng, Double dropoffLat, Double dropoffLng) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-Api-Key", googleMapsApiKey);
        headers.set("X-Goog-FieldMask", "routes.distanceMeters,routes.duration");

        Map<String, Object> body = new HashMap<>();
        body.put("origin", buildWaypoint(pickupLat, pickupLng));
        body.put("destination", buildWaypoint(dropoffLat, dropoffLng));
        body.put("travelMode", "DRIVE");
        body.put("routingPreference", "TRAFFIC_UNAWARE");
        body.put("computeAlternativeRoutes", false);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                googleRoutesUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode routes = root.path("routes");

            if (!routes.isArray() || routes.isEmpty()) {
                throw new RuntimeException("No routes returned from Google Routes API.");
            }

            JsonNode firstRoute = routes.get(0);
            long distanceMeters = firstRoute.path("distanceMeters").asLong();
            String durationRaw = firstRoute.path("duration").asText();

            long durationSeconds = parseDurationSeconds(durationRaw);

            return new RouteInfoResponse(distanceMeters, durationSeconds);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Google Routes API response.", e);
        }
    }

    private Map<String, Object> buildWaypoint(Double lat, Double lng) {
        Map<String, Object> latLng = new HashMap<>();
        latLng.put("latitude", lat);
        latLng.put("longitude", lng);

        Map<String, Object> location = new HashMap<>();
        location.put("latLng", latLng);

        Map<String, Object> waypoint = new HashMap<>();
        waypoint.put("location", location);

        return waypoint;
    }

    private long parseDurationSeconds(String durationRaw) {
        if (durationRaw == null || durationRaw.isBlank()) {
            return 0L;
        }

        if (durationRaw.endsWith("s")) {
            return Long.parseLong(durationRaw.substring(0, durationRaw.length() - 1));
        }

        throw new IllegalArgumentException("Unexpected duration format: " + durationRaw);
    }
}

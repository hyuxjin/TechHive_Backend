package com.example.admin_backend.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/location")
@CrossOrigin(origins = "http://localhost:3000")
public class GoogleAPIController {

    // Fetch the API key from the application.properties file
    @Value("${google.maps.api.key}")
    private String googleApiKey;

    // Endpoint to get geocode based on latitude and longitude
    @GetMapping("/geocode")
    public String getGeocode(@RequestParam("lat") Double latitude, @RequestParam("lng") Double longitude) {
        // Build the Google Maps Geocode API URL
        String apiUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + googleApiKey;

        // Use RestTemplate to call the API and return the response
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(apiUrl, String.class);
    }
}

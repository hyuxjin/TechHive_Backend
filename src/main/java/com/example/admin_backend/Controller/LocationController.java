package com.example.admin_backend.Controller;

import com.example.admin_backend.Entity.LocationEntity;
import com.example.admin_backend.Service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    // Save user's location based on latitude, longitude, idNumber, and buildingName
    @PostMapping("/user-location")
    public ResponseEntity<LocationEntity> saveUserLocation(@RequestBody Map<String, Object> locationData) {
        Double latitude = (Double) locationData.get("latitude");
        Double longitude = (Double) locationData.get("longitude");
        String idNumber = (String) locationData.get("idNumber");
        String buildingName = (String) locationData.get("buildingName"); // Add building name

        if (latitude == null || longitude == null || idNumber == null || buildingName == null) {
            return ResponseEntity.badRequest().body(null);  // If missing parameters
        }

        // Pass the building name along with the latitude, longitude, and idNumber
        LocationEntity savedLocation = locationService.saveUserLocation(latitude, longitude, idNumber, buildingName);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLocation);
    }

    // Get all stored locations
    @GetMapping
    public ResponseEntity<List<LocationEntity>> getAllLocations() {
        List<LocationEntity> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    // Get a location by ID
    @GetMapping("/{id}")
    public ResponseEntity<LocationEntity> getLocationById(@PathVariable Long id) {
        LocationEntity location = locationService.getLocationById(id);
        if (location != null) {
            return ResponseEntity.ok(location);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Check the nearest building for a given latitude and longitude
    @GetMapping("/check-location")
    public ResponseEntity<String> checkLocation(@RequestParam Double latitude, @RequestParam Double longitude) {
        String nearestBuilding = locationService.findNearestBuilding(latitude, longitude);
        if (nearestBuilding.equals("Outside Campus")) {
            return ResponseEntity.ok("Outside Campus");
        }
        return ResponseEntity.ok("Inside Campus: " + nearestBuilding);
    }

    // Update location by ID
    @PutMapping("/{id}")
    public ResponseEntity<LocationEntity> updateLocation(@PathVariable Long id, @RequestParam Double latitude, @RequestParam Double longitude) {
        LocationEntity updatedLocation = locationService.updateLocation(id, latitude, longitude);
        if (updatedLocation != null) {
            return ResponseEntity.ok(updatedLocation);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Delete location by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        boolean isDeleted = locationService.deleteLocation(id);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

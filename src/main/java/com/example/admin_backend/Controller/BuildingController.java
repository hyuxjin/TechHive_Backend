package com.example.admin_backend.Controller;

import com.example.admin_backend.Entity.BuildingEntity;
import com.example.admin_backend.Service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    // 1. Add a new building
    @PostMapping("/add")
    public ResponseEntity<BuildingEntity> addBuilding(@RequestBody BuildingEntity buildingEntity) {
        BuildingEntity savedBuilding = buildingService.addOrUpdateBuilding(buildingEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBuilding);
    }

    // 2. Get all buildings
    @GetMapping("/all")
    public ResponseEntity<List<BuildingEntity>> getAllBuildings() {
        List<BuildingEntity> buildings = buildingService.getAllBuildings();
        return ResponseEntity.ok(buildings);
    }

    // 3. Get predefined buildings
    @GetMapping("/predefined")
    public ResponseEntity<List<BuildingEntity>> getPredefinedBuildings() {
        List<BuildingEntity> predefinedBuildings = buildingService.getPredefinedBuildings();
        return ResponseEntity.ok(predefinedBuildings);
    }

    // 4. Soft delete a building by ID
    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<Void> softDeleteBuildingById(@PathVariable Long id) {
        boolean isDeleted = buildingService.softDeleteBuildingById(id);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 5. Soft delete a building by name
    @DeleteMapping("/soft-delete-by-name")
    public ResponseEntity<Void> softDeleteBuildingByName(@RequestParam String buildingName) {
        boolean isDeleted = buildingService.softDeleteBuildingByName(buildingName);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

package com.example.admin_backend;

import com.example.admin_backend.Service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PredefinedBuildingLoader implements CommandLineRunner {

    @Autowired
    private BuildingService buildingService;

    @Override
    public void run(String... args) throws Exception {
        // Load predefined buildings into the database at startup
        buildingService.loadPredefinedBuildings();
    }
}

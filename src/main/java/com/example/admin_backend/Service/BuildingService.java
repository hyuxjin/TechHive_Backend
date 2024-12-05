package com.example.admin_backend.Service;

import com.example.admin_backend.Entity.BuildingEntity;
import com.example.admin_backend.Repository.BuildingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class BuildingService {

    @Autowired
    private BuildingRepository buildingRepository;

    // Fetch all non-deleted buildings
    public List<BuildingEntity> getAllBuildings() {
        return buildingRepository.findByIsDeletedFalse();
    }

    // Add or update building details
    public BuildingEntity addOrUpdateBuilding(BuildingEntity buildingEntity) {
        return buildingRepository.save(buildingEntity);
    }

    // Fetch predefined buildings
    public List<BuildingEntity> getPredefinedBuildings() {
        List<BuildingEntity> predefinedBuildings = new ArrayList<>();
        
        predefinedBuildings.add(new BuildingEntity("Cebu Institute of Technology - University", 10.29448734601167, 123.88109339817153));
        predefinedBuildings.add(new BuildingEntity("CIT-University ST Building", 10.294278202699603, 123.88102433129116));
        predefinedBuildings.add(new BuildingEntity("CIT-U Science and Technology Parking Lot", 10.294422689734859, 123.88092911286671));
        predefinedBuildings.add(new BuildingEntity("CIT-U BDO ATM", 10.294544958908151, 123.88092286690699));
        predefinedBuildings.add(new BuildingEntity("CIT-U Accounting Office", 10.29456211260864, 123.88095505341302));
        predefinedBuildings.add(new BuildingEntity("College of Engineering and Architecture", 10.294521867386786, 123.88083837732863));
        predefinedBuildings.add(new BuildingEntity("Gregorio L. Escario Building", 10.295246980726224, 123.8813573976815));
        predefinedBuildings.add(new BuildingEntity("CIT-U Chapel", 10.295776104815927, 123.88088130558833));
        predefinedBuildings.add(new BuildingEntity("CIT-U College of Engineering and Architecture (CEA) Office", 10.295793258449457, 123.88099127615062));
        predefinedBuildings.add(new BuildingEntity("CENTRAL VISAYAS FOOD INNOVATION CENTER", 10.295905581518241, 123.88075619274562));
        predefinedBuildings.add(new BuildingEntity("CIT-U Main Canteen", 10.296059066812777, 123.88059722363963));
        predefinedBuildings.add(new BuildingEntity("PE Classroom", 10.295803136473827, 123.88073355391082));
        predefinedBuildings.add(new BuildingEntity("Parking Lot", 10.295584804631165, 123.88045598471643));
        predefinedBuildings.add(new BuildingEntity("CIT-U Basketball Court", 10.295717415481983, 123.88011802634655));
        predefinedBuildings.add(new BuildingEntity("CIT-U Junior High School Building", 10.295615153388963, 123.87984377045392));
        predefinedBuildings.add(new BuildingEntity("CIT-U Chemical Engineering Department", 10.295391496174522, 123.87968216735187));
        predefinedBuildings.add(new BuildingEntity("CIT-U Mechanical Engineering Department", 10.29583516750188, 123.87961855341084));
        predefinedBuildings.add(new BuildingEntity("CIT-U Mining Engineering Department", 10.295974375794053, 123.87950321842133));
        predefinedBuildings.add(new BuildingEntity("CIT-U Civil Engineering Department", 10.294858545377364, 123.87981040838321));
        predefinedBuildings.add(new BuildingEntity("CIT-U College Library", 10.295126406663186, 123.88045279749396));
        predefinedBuildings.add(new BuildingEntity("CIT-U Main Building", 10.295225785037593, 123.88086021536141));
        predefinedBuildings.add(new BuildingEntity("CIT-U Volleyball Court", 10.294730307611553, 123.88057388951488));
        predefinedBuildings.add(new BuildingEntity("CIT-U Gymnasium", 10.29606952351622, 123.88019898831824));
        predefinedBuildings.add(new BuildingEntity("Elementary Building", 10.296499511220345, 123.88017746133305));
        predefinedBuildings.add(new BuildingEntity("CIT-U University Playground", 10.296317596179035, 123.88050156701337));
        predefinedBuildings.add(new BuildingEntity("CIT-U High School Canteen", 10.296393572475106, 123.87983812919866));
        predefinedBuildings.add(new BuildingEntity("CIT-U Mini Canteen", 10.294225565660339, 123.8802590315609));
        predefinedBuildings.add(new BuildingEntity("CIT-U Science and Technology Building", 10.29433294534296, 123.88120733957837));
        predefinedBuildings.add(new BuildingEntity("Espacio CIT", 10.295623034050386, 123.88069592179068));
       
        return predefinedBuildings;
    }

    // Load predefined buildings into the database
    public void loadPredefinedBuildings() {
        List<BuildingEntity> predefinedBuildings = getPredefinedBuildings();

        for (BuildingEntity predefinedBuilding : predefinedBuildings) {
            // Check if the building already exists
            Optional<BuildingEntity> existingBuilding = buildingRepository.findByBuildingName(predefinedBuilding.getBuildingName()).stream().findFirst();

            if (!existingBuilding.isPresent()) {
                // If it doesn't exist, save the building
                buildingRepository.save(predefinedBuilding);
            }
        }
    }

    // Soft delete a building by ID
    public boolean softDeleteBuildingById(Long id) {
        BuildingEntity building = buildingRepository.findById(id).orElse(null);
        if (building != null && !building.isDeleted()) {
            building.setDeleted(true);
            buildingRepository.save(building);
            return true;
        }
        return false;
    }

    // Soft delete a building by name
    public boolean softDeleteBuildingByName(String buildingName) {
        List<BuildingEntity> buildings = buildingRepository.findByBuildingNameAndIsDeletedFalse(buildingName);
        if (!buildings.isEmpty()) {
            for (BuildingEntity building : buildings) {
                building.setDeleted(true);
                buildingRepository.save(building);
            }
            return true;
        }
        return false;
    }

}

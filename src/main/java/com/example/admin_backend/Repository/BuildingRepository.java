package com.example.admin_backend.Repository;

import com.example.admin_backend.Entity.BuildingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<BuildingEntity, Long> {
    List<BuildingEntity> findAll();  // To fetch all buildings and their coordinates
    
    List<BuildingEntity> findByBuildingName(String buildingName);
    // Fetch only non-deleted buildings
    List<BuildingEntity> findByIsDeletedFalse();

    // Find buildings by name, excluding deleted ones
    List<BuildingEntity> findByBuildingNameAndIsDeletedFalse(String buildingName);
}

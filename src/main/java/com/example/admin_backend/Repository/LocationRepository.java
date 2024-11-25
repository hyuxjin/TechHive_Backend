package com.example.admin_backend.Repository;

import com.example.admin_backend.Entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    
    // Find locations based on latitude and longitude
    List<LocationEntity> findByLatitudeAndLongitude(double latitude, double longitude);
    
    // Find locations by user's idNumber (this is now in the UserEntity)
    List<LocationEntity> findByUser_IdNumber(String idNumber);
}

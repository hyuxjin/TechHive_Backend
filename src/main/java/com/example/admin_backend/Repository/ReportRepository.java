package com.example.admin_backend.Repository;

import com.example.admin_backend.Entity.ReportEntity;
import com.example.admin_backend.Entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<ReportEntity, Integer> {

    // Corrected method to compare with the enum type ReportStatus
    List<ReportEntity> findByStatus(ReportStatus status);  // Compare with the enum type, not a String

    List<ReportEntity> findByUser_UserId(int userId);

    // Count reports by status and user ID
    int countByStatusAndUser_UserId(ReportStatus status, int userId);
    
}


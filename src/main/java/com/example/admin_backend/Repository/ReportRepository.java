package com.example.admin_backend.Repository;

import com.example.admin_backend.Entity.ReportEntity;
import com.example.admin_backend.Entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ReportRepository extends JpaRepository<ReportEntity, Integer> {
    // Basic queries
    List<ReportEntity> findByStatus(ReportStatus status);
    List<ReportEntity> findByUser_UserId(int userId);
    int countByStatusAndUser_UserId(ReportStatus status, int userId);

    // Custom queries for reports with sorting
    @Query("SELECT r FROM ReportEntity r ORDER BY r.submittedAt DESC")
    List<ReportEntity> findAllOrderBySubmittedAtDesc();

    @Query("SELECT r FROM ReportEntity r WHERE r.status = 'PENDING' ORDER BY r.submittedAt DESC")
    List<ReportEntity> findAllPendingReports();

    // Count queries
    long countByStatus(ReportStatus status);

    // Find by location
    List<ReportEntity> findByLocation(String location);

    // Find by concerned office
    List<ReportEntity> findByConcernedOffice(String concernedOffice);

    // Find by user and status
    List<ReportEntity> findByUser_UserIdAndStatus(int userId, ReportStatus status);

    // Find reports with images
    @Query("SELECT r FROM ReportEntity r WHERE r.image1Path IS NOT NULL OR r.image2Path IS NOT NULL OR r.image3Path IS NOT NULL ORDER BY r.submittedAt DESC")
    List<ReportEntity> findReportsWithImages();

    // Search reports
    @Query("SELECT r FROM ReportEntity r WHERE LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY r.submittedAt DESC")
    List<ReportEntity> searchByDescriptionKeyword(String keyword);
}
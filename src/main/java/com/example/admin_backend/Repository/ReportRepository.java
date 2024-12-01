package com.example.admin_backend.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.admin_backend.Entity.ReportEntity;
import com.example.admin_backend.Entity.ReportStatus;

public interface ReportRepository extends JpaRepository<ReportEntity, Integer> {
    // Basic CRUD operations are inherited from JpaRepository
   Optional<ReportEntity> findByReportId(int reportId);

    // Status-based queries
    List<ReportEntity> findByStatus(ReportStatus status);
    List<ReportEntity> findByStatusAndUser_UserId(ReportStatus status, int userId);
    
    //UPDATED FOR USER// Count reports by status and user ID
    int countByStatusAndUser_UserId(ReportStatus status, int userId);
    
        List<ReportEntity> findByStatusAndSubmittedAtBetween(
        ReportStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    // Custom queries with sorting
    @Query("SELECT r FROM ReportEntity r ORDER BY r.submittedAt DESC")
    List<ReportEntity> findAllOrderBySubmittedAtDesc();

    @Query("SELECT r FROM ReportEntity r WHERE r.status IN ('PENDING', 'ACKNOWLEDGED') ORDER BY r.submittedAt DESC")
    List<ReportEntity> findAllPendingReports();

    @Query("SELECT r FROM ReportEntity r WHERE r.status = 'IN_PROGRESS' ORDER BY r.submittedAt DESC")
    List<ReportEntity> findAllInProgressReports();

    @Query("SELECT r FROM ReportEntity r WHERE r.status = 'RESOLVED' ORDER BY r.submittedAt DESC")
    List<ReportEntity> findAllResolvedReports();

    // Count queries
    long countByStatus(ReportStatus status);

    // Location-based queries
    List<ReportEntity> findByLocation(String location);

    // Office-based queries
    List<ReportEntity> findByConcernedOffice(String concernedOffice);

    // User-based queries
    List<ReportEntity> findByUser_UserId(int userId);
    List<ReportEntity> findByUser_UserIdAndStatus(int userId, ReportStatus status);

    //UPDATE FOR USER
    int countByStatusAndSubmittedAtBetween(ReportStatus status, LocalDateTime startDate, LocalDateTime endDate);

    // Image-based queries
    @Query("SELECT r FROM ReportEntity r WHERE r.image1Path IS NOT NULL OR r.image2Path IS NOT NULL OR r.image3Path IS NOT NULL ORDER BY r.submittedAt DESC")
    List<ReportEntity> findReportsWithImages();

    // Search queries
    @Query("SELECT r FROM ReportEntity r WHERE LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY r.submittedAt DESC")
    List<ReportEntity> searchByDescriptionKeyword(@Param("keyword") String keyword);
}
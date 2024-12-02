package com.example.admin_backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.admin_backend.Entity.ReportEntity;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    // Find reports that are approved
    List<ReportEntity> findByApproved(Boolean approved);

    // Find reports that are declined
    List<ReportEntity> findByDeclined(Boolean declined);

    // Find reports that are both approved and declined (pending)
    List<ReportEntity> findByApprovedAndDeclined(Boolean approved, Boolean declined);
}
package com.example.admin_backend.Service;

import java.util.List;

import org.springframework.stereotype.Service; // Added import statement

import com.example.admin_backend.Entity.ReportEntity;
import com.example.admin_backend.Repository.ReportRepository;

@Service
public class ReportService {
    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<ReportEntity> getAllPendingReports() {
        return reportRepository.findByApprovedAndDeclined(false, false);
    }

    public List<ReportEntity> getAllApprovedReports() {
        return reportRepository.findByApproved(true);
    }

    public List<ReportEntity> getAllDeclinedReports() {
        return reportRepository.findByDeclined(true);
    }

    public ReportEntity approveReport(Long reportId) {
        return reportRepository.findById(reportId)
                .map(report -> {
                    report.setApproved(true);
                    report.setDeclined(false);
                    return reportRepository.save(report);
                }).orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));
    }

    public ReportEntity declineReport(Long reportId) {
        return reportRepository.findById(reportId)
                .map(report -> {
                    report.setApproved(false);
                    report.setDeclined(true);
                    return reportRepository.save(report);
                }).orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));
    }
}
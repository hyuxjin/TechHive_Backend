package com.example.admin_backend.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.admin_backend.Entity.ReportEntity;
import com.example.admin_backend.Service.ReportService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/admin/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ReportEntity>> getPendingReports() {
        List<ReportEntity> reports = reportService.getAllPendingReports();
        if (reports.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/approved")
    public ResponseEntity<List<ReportEntity>> getApprovedReports() {
        List<ReportEntity> reports = reportService.getAllApprovedReports();
        if (reports.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/declined")
    public ResponseEntity<List<ReportEntity>> getDeclinedReports() {
        List<ReportEntity> reports = reportService.getAllDeclinedReports();
        if (reports.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reports);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<ReportEntity> approveReport(@PathVariable Long id) {
        ReportEntity report = reportService.approveReport(id);
        return ResponseEntity.ok(report);
    }

    @PutMapping("/decline/{id}")
    public ResponseEntity<ReportEntity> declineReport(@PathVariable Long id) {
        ReportEntity report = reportService.declineReport(id);
        return ResponseEntity.ok(report);
    }
}
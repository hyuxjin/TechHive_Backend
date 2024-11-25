package com.example.admin_backend.Controller;

import com.example.admin_backend.Entity.PostEntity;
import com.example.admin_backend.Entity.ReportEntity;
import com.example.admin_backend.Entity.UserEntity;
import com.example.admin_backend.Repository.ReportRepository;
import com.example.admin_backend.Service.PostService;
import com.example.admin_backend.Service.UserReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.admin_backend.Entity.ReportStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/reports")
@CrossOrigin(origins = "http://localhost:3000") // Add this for frontend integration
public class UserReportController {

    private final UserReportService userReportService;
    private final PostService postService;
    private final ReportRepository reportRepository;

    public UserReportController(
            UserReportService userReportService, 
            PostService postService,
            ReportRepository reportRepository
    ) {
        this.userReportService = userReportService;
        this.postService = postService;
        this.reportRepository = reportRepository;
    }

    // Get all reports
    @GetMapping
    public ResponseEntity<?> getAllReports() {
        try {
            List<ReportEntity> reports = reportRepository.findAll();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch reports: " + e.getMessage());
            errorResponse.put("details", e.getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    // Submit a new report
    @PostMapping(value = "/submit", consumes = "multipart/form-data")
    public ResponseEntity<?> submitReport(
            @RequestParam("description") String description,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam("buildingName") String buildingName,
            @RequestParam("userId") int userId,
            @RequestParam(value = "image1", required = false) MultipartFile image1,
            @RequestParam(value = "image2", required = false) MultipartFile image2,
            @RequestParam(value = "image3", required = false) MultipartFile image3) {

        try {
            // Validate user
            UserEntity user = userReportService.findUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "User not found"));
            }

            // Handle image uploads
            List<String> imagePaths = new ArrayList<>();
            try {
                if (image1 != null && !image1.isEmpty()) imagePaths.add(saveImage(image1));
                if (image2 != null && !image2.isEmpty()) imagePaths.add(saveImage(image2));
                if (image3 != null && !image3.isEmpty()) imagePaths.add(saveImage(image3));
            } catch (IOException e) {
                System.err.println("Error saving images: " + e.getMessage());
            }

            // Create post with null checks
            PostEntity submittedReportPost = new PostEntity(); 
            submittedReportPost.setContent(description != null ? description : ""); 
            submittedReportPost.setUserId(userId); 
            submittedReportPost.setFullName(user.getFullName() != null ? user.getFullName() : ""); 
            submittedReportPost.setIdNumber(user.getIdNumber() != null ? user.getIdNumber() : "");
            String userRole = (user.getRole() != null) ? user.getRole().toUpperCase() : "USER";
            submittedReportPost.setUserRole(userRole);
            submittedReportPost.setTimestamp(LocalDateTime.now()); 
            submittedReportPost.setImage(!imagePaths.isEmpty() ? imagePaths.get(0) : null); 
            submittedReportPost.setIsSubmittedReport(true); 
            submittedReportPost.setStatus("Pending");
            submittedReportPost.setVisible(true);

            // Save post and report
            PostEntity savedPost = postService.createPost(submittedReportPost);
            if (savedPost == null) {
                throw new RuntimeException("Failed to create post entry for the report");
            }

            ReportEntity report = userReportService.submitReport(description, imagePaths, user, latitude, longitude, buildingName);

            // Create response map
            Map<String, Object> response = new HashMap<>();
            response.put("report", report);
            response.put("post", savedPost);
            response.put("message", "Report submitted successfully" + 
                (imagePaths.isEmpty() ? " (without images due to upload error)" : ""));
            response.put("imagePaths", imagePaths);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to submit report: " + e.getMessage());
            errorResponse.put("details", e.getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
        }
    }

    // Get reports by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReportsByUser(@PathVariable int userId) {
        try {
            List<ReportEntity> reports = userReportService.getReportsByUserId(userId);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch reports: " + e.getMessage()));
        }
    }

    // Get report status counts
    @GetMapping("/reportStatusCounts/{userId}")
    public ResponseEntity<?> getReportStatusCounts(@PathVariable int userId) {
        try {
            Map<String, Integer> statusCounts = userReportService.getReportStatusCounts(userId);
            return ResponseEntity.ok(statusCounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch status counts: " + e.getMessage()));
        }
    }

    // Update report status
    @PutMapping("/{reportId}/status")
    public ResponseEntity<?> updateReportStatus(
            @PathVariable int reportId,
            @RequestBody Map<String, String> statusUpdate) {
        try {
            ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
            
            report.setStatus(ReportStatus.valueOf(statusUpdate.get("status")));
            ReportEntity updatedReport = reportRepository.save(report);
            
            return ResponseEntity.ok(updatedReport);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update report status: " + e.getMessage()));
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Invalid image file");
        }

        String uploadDir = System.getProperty("user.home") + File.separator + "thetechhive_uploads";
        File directory = new File(uploadDir);
        
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IOException("Failed to create upload directory");
            }
        }

        if (!directory.exists() || !directory.canWrite()) {
            throw new IOException("Upload directory is not accessible or writable: " + uploadDir);
        }

        try {
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String fileName = System.currentTimeMillis() + "_" + Math.round(Math.random() * 1000) + extension;
            
            Path filePath = Paths.get(uploadDir, fileName);
            Files.createDirectories(filePath.getParent());
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("Debug - Saved image at: " + filePath.toString());
            return "/uploads/" + fileName;

        } catch (IOException e) {
            System.err.println("Failed to save image: " + e.getMessage());
            throw new IOException("Failed to save image: " + e.getMessage());
        }
    }
}
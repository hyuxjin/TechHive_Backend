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
@CrossOrigin(origins = "http://localhost:3000")
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

    @GetMapping
    public ResponseEntity<?> getAllReports() {
        try {
            List<ReportEntity> reports = reportRepository.findAllOrderBySubmittedAtDesc();
            
            System.out.println("\n=== Fetching All Reports ===");
            System.out.println("Total reports found: " + reports.size());
            
            if (reports.isEmpty()) {
                System.out.println("No reports found in database");
                return ResponseEntity.ok()
                    .header("X-Total-Count", "0")
                    .body(new ArrayList<>());
            }
            
            reports.forEach(report -> {
                System.out.println("\nReport Details:");
                System.out.println("ID: " + report.getReportId());
                System.out.println("User: " + report.getUserFullName());
                System.out.println("Description: " + report.getDescription());
                System.out.println("Status: " + report.getStatus());
                System.out.println("Location: " + report.getLocation());
                System.out.println("Images: [" + 
                    report.getImage1Path() + ", " + 
                    report.getImage2Path() + ", " + 
                    report.getImage3Path() + "]");
            });

            return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(reports.size()))
                .body(reports);

        } catch (Exception e) {
            System.err.println("Error fetching reports: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch reports: " + e.getMessage());
            errorResponse.put("details", e.getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

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

        System.out.println("\n=== Submitting New Report ===");
        System.out.println("Description: " + description);
        System.out.println("Building Name: " + buildingName);
        System.out.println("User ID: " + userId);

        try {
            UserEntity user = userReportService.findUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "User not found"));
            }

            List<String> imagePaths = new ArrayList<>();
            if (image1 != null && !image1.isEmpty()) {
                String path = saveImage(image1);
                imagePaths.add(path);
                System.out.println("Image 1 saved: " + path);
            }
            if (image2 != null && !image2.isEmpty()) {
                String path = saveImage(image2);
                imagePaths.add(path);
                System.out.println("Image 2 saved: " + path);
            }
            if (image3 != null && !image3.isEmpty()) {
                String path = saveImage(image3);
                imagePaths.add(path);
                System.out.println("Image 3 saved: " + path);
            }

            PostEntity submittedReportPost = new PostEntity(); 
            submittedReportPost.setContent(description != null ? description : ""); 
            submittedReportPost.setUserId(userId); 
            submittedReportPost.setFullName(user.getFullName() != null ? user.getFullName() : ""); 
            submittedReportPost.setIdNumber(user.getIdNumber() != null ? user.getIdNumber() : "");
            submittedReportPost.setUserRole(user.getRole() != null ? user.getRole().toUpperCase() : "USER");
            submittedReportPost.setTimestamp(LocalDateTime.now()); 
            submittedReportPost.setImage(!imagePaths.isEmpty() ? imagePaths.get(0) : null); 
            submittedReportPost.setIsSubmittedReport(true); 
            submittedReportPost.setStatus("Pending");
            submittedReportPost.setVisible(true);

            PostEntity savedPost = postService.createPost(submittedReportPost);
            if (savedPost == null) {
                throw new RuntimeException("Failed to create post entry for the report");
            }

            ReportEntity report = userReportService.submitReport(description, imagePaths, user, latitude, longitude, buildingName);
            System.out.println("Report created with ID: " + report.getReportId());

            Map<String, Object> response = new HashMap<>();
            response.put("report", report);
            response.put("post", savedPost);
            response.put("message", "Report submitted successfully" + 
                (imagePaths.isEmpty() ? " (without images)" : " with " + imagePaths.size() + " images"));
            response.put("imagePaths", imagePaths);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error submitting report: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to submit report: " + e.getMessage());
            errorResponse.put("details", e.getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReportsByUser(@PathVariable int userId) {
        try {
            List<ReportEntity> reports = userReportService.getReportsByUserId(userId);
            System.out.println("Fetched " + reports.size() + " reports for user " + userId);
            return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(reports.size()))
                .body(reports);
        } catch (Exception e) {
            System.err.println("Error fetching user reports: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch reports: " + e.getMessage()));
        }
    }

    @GetMapping("/reportStatusCounts/{userId}")
    public ResponseEntity<?> getReportStatusCounts(@PathVariable int userId) {
        try {
            Map<String, Integer> statusCounts = userReportService.getReportStatusCounts(userId);
            return ResponseEntity.ok(statusCounts);
        } catch (Exception e) {
            System.err.println("Error fetching status counts: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch status counts: " + e.getMessage()));
        }
    }

    @PutMapping("/{reportId}/status")
    public ResponseEntity<?> updateReportStatus(
            @PathVariable int reportId,
            @RequestBody Map<String, String> statusUpdate) {
        try {
            System.out.println("Updating status for report " + reportId + " to " + statusUpdate.get("status"));
            
            ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
            
            report.setStatus(ReportStatus.valueOf(statusUpdate.get("status")));
            ReportEntity updatedReport = reportRepository.save(report);
            
            System.out.println("Status updated successfully");
            return ResponseEntity.ok(updatedReport);
        } catch (Exception e) {
            System.err.println("Error updating status: " + e.getMessage());
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
                throw new IOException("Failed to create upload directory: " + uploadDir);
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
            
            System.out.println("Image saved successfully at: " + filePath.toString());
            return "/uploads/" + fileName;

        } catch (IOException e) {
            System.err.println("Failed to save image: " + e.getMessage());
            throw new IOException("Failed to save image: " + e.getMessage());
        }
    }
}
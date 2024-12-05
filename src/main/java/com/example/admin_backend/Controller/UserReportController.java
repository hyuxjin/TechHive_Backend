package com.example.admin_backend.Controller;

import com.example.admin_backend.Entity.PostEntity;
import com.example.admin_backend.Entity.ReportEntity;
import com.example.admin_backend.Entity.UserEntity;
import com.example.admin_backend.Repository.ReportRepository;
import com.example.admin_backend.Service.PostService;
import com.example.admin_backend.Service.UserReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.admin_backend.Entity.ReportStatus;

import java.io.File;
import java.io.IOException;
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
    private final ObjectMapper objectMapper;

    public UserReportController(
            UserReportService userReportService, 
            PostService postService,
            ReportRepository reportRepository,
            ObjectMapper objectMapper) {
        this.userReportService = userReportService;
        this.postService = postService;
        this.reportRepository = reportRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<?> getAllReports() {
        try {
            System.out.println("\n=== DEBUG: GET ALL REPORTS - Start ===");
            List<ReportEntity> reports = reportRepository.findAllOrderBySubmittedAtDesc();
            
            // Debug logging for the reports list
            System.out.println("Reports object type: " + reports.getClass().getName());
            System.out.println("Number of reports: " + reports.size());
            System.out.println("Is reports null? " + (reports == null));
            System.out.println("Is reports empty? " + reports.isEmpty());
            
            // Log each report
            reports.forEach(report -> {
                System.out.println("\nReport Details:");
                System.out.println("ID: " + report.getReportId());
                System.out.println("User: " + report.getUserFullName());
                System.out.println("Status: " + report.getStatus());
                System.out.println("Description: " + report.getDescription());
            });

            // Debug log the JSON that will be sent
            String jsonResponse = objectMapper.writeValueAsString(reports);
            System.out.println("\nJSON Response to be sent:");
            System.out.println(jsonResponse);
            
            if (reports.isEmpty()) {
                System.out.println("No reports found in database");
                return ResponseEntity.ok()
                    .header("X-Total-Count", "0")
                    .body(new ArrayList<>());
            }

            ResponseEntity<?> response = ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(reports.size()))
                .body(reports);

            System.out.println("\nResponse body type: " + response.getBody().getClass().getName());
            System.out.println("=== DEBUG: GET ALL REPORTS - End ===\n");

            return response;

        } catch (Exception e) {
            System.err.println("\n=== ERROR in getAllReports ===");
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch reports: " + e.getMessage());
            errorResponse.put("details", e.getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingReports() {
        try {
            List<ReportEntity> reports = reportRepository.findAllPendingReports();
            
            System.out.println("\n=== Fetching Pending Reports ===");
            System.out.println("Total pending reports found: " + reports.size());
            
            return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(reports.size()))
                .body(reports);
        } catch (Exception e) {
            System.err.println("Error fetching pending reports: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch pending reports: " + e.getMessage()));
        }
    }

    @GetMapping("/in-progress")
    public ResponseEntity<?> getInProgressReports() {
        try {
            List<ReportEntity> reports = reportRepository.findAllInProgressReports();
            
            System.out.println("\n=== Fetching In-Progress Reports ===");
            System.out.println("Total in-progress reports found: " + reports.size());
            
            return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(reports.size()))
                .body(reports);
        } catch (Exception e) {
            System.err.println("Error fetching in-progress reports: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch in-progress reports: " + e.getMessage()));
        }
    }

    @GetMapping("/resolved")
    public ResponseEntity<?> getResolvedReports() {
        try {
            List<ReportEntity> reports = reportRepository.findAllResolvedReports();
            
            System.out.println("\n=== Fetching Resolved Reports ===");
            System.out.println("Total resolved reports found: " + reports.size());
            
            return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(reports.size()))
                .body(reports);
        } catch (Exception e) {
            System.err.println("Error fetching resolved reports: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch resolved reports: " + e.getMessage()));
        }
    }
@PutMapping("/{id}/flag")
    public ResponseEntity<?> updateFlagStatus(@PathVariable int id, @RequestBody Map<String, Boolean> payload) {
        Boolean isFlagged = payload.get("isFlagged");
        ReportEntity updatedReport = userReportService.updateFlagStatus(id, isFlagged);
        if (updatedReport != null) {
            return ResponseEntity.ok(updatedReport);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Report not found");
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

            // Create a corresponding post entry for the submitted report 
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

            postService.createPost(submittedReportPost);

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

    // Fetch all reports submitted by a user
    @GetMapping("/user/{userId}")
    public List<ReportEntity> getReportsByUser(@PathVariable int userId) {
        return userReportService.getReportsByUserId(userId);
    }

    @GetMapping("/reportStatusCounts/{userId}")
    public ResponseEntity<Map<String, Integer>> getReportStatusCounts(@PathVariable int userId) {
        Map<String, Integer> statusCounts = userReportService.getReportStatusCounts(userId);
        return ResponseEntity.ok(statusCounts);
    }

    // Fetch all pending reports
    @GetMapping("/pending/monthly")
    public ResponseEntity<List<Map<String, Object>>> getPendingReportsGroupedByMonth() {
        try {
            List<Map<String, Object>> pendingReportsByMonth = new ArrayList<>();
            
            // Loop through each month (1 to 12) for the year 2024
            for (int month = 1; month <= 12; month++) {
                Map<String, Object> monthData = new HashMap<>();
                
                // Calculate the start and end of the month using YearMonth
                LocalDateTime startOfMonth = LocalDateTime.of(2024, month, 1, 0, 0);
                LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);
                
                // Log the date range for debugging
                System.out.println("Fetching reports for month: " + month);
                System.out.println("Start of month: " + startOfMonth);
                System.out.println("End of month: " + endOfMonth);
                
                try {
                    // Get the count of pending reports for the given month
                    long count = reportRepository.countByStatusAndSubmittedAtBetween(
                        ReportStatus.PENDING,
                        startOfMonth,
                        endOfMonth
                    );
                    
                    monthData.put("month", month);
                    monthData.put("count", count);
                    pendingReportsByMonth.add(monthData);
                } catch (Exception e) {
                    // Log the error for this specific month but continue processing
                    System.err.println("Error processing month " + month + ": " + e.getMessage());
                    monthData.put("month", month);
                    monthData.put("count", 0);  // Default to 0 for error cases
                    monthData.put("error", "Failed to fetch data");
                    pendingReportsByMonth.add(monthData);
                }
            }
            
            if (pendingReportsByMonth.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            
            return ResponseEntity.ok(pendingReportsByMonth);
            
        } catch (Exception e) {
            System.err.println("Error occurred while fetching pending reports by month: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(List.of(Map.of("error", "Failed to fetch monthly report data: " + e.getMessage())));
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
    
        // Define the upload directory path
        String uploadDir = "C:\\Users\\corbe\\ADMIN_TECHHIVE_FRONTEND\\public\\Upload_report";
        File directory = new File(uploadDir);
    
        // Create the directory if it doesn't exist
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IOException("Failed to create upload directory: " + uploadDir);
            }
        }
    
        // Check if the directory is writable
        if (!directory.canWrite()) {
            throw new IOException("Upload directory is not writable: " + uploadDir);
        }
    
        try {
            // Extract the original file name
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg"; // Default to .jpg if no extension is provided
    
            // Generate a unique file name
            String fileName = System.currentTimeMillis() + "_" + Math.round(Math.random() * 1000) + extension;
    
            // Set the full file path
            String fullFilePath = uploadDir + File.separator + fileName;
    
            // Save the file
            image.transferTo(new File(fullFilePath));
    
            System.out.println("Image saved successfully at: " + fullFilePath);
    
            // Return the relative path (used by the frontend)
            return "/Upload_report/" + fileName;
    
        } catch (IOException e) {
            System.err.println("Failed to save image: " + e.getMessage());
            throw new IOException("Failed to save image: " + e.getMessage());
        }
    }
}
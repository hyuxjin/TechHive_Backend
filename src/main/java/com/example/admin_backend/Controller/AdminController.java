package com.example.admin_backend.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Service.AdminService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AdminController {

    private static final String SESSION_USER_KEY = "admin";
    private static final String JSESSIONID_PREFIX = "JSESSIONID=";
    private static final int SESSION_TIMEOUT = 30 * 60; // 30 minutes in seconds

    @Autowired
    private AdminService adminService;

    // Helper method to check session
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute(SESSION_USER_KEY) != null;
    }

    @GetMapping("/print")
    public String itWorks() {
        return "It works";
    }

    // Sign-in with session
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody Map<String, String> loginData, HttpSession session) {
        String idNumber = loginData.get("idNumber");
        String password = loginData.get("password");

        try {
            AdminEntity admin = adminService.getAdminByIdNumberAndPassword(idNumber, password);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID Number or password.");
            }

            if (!admin.getStatus()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is disabled.");
            }

            // Configure session
            session.setAttribute(SESSION_USER_KEY, admin);
            session.setMaxInactiveInterval(SESSION_TIMEOUT);

            Map<String, Object> response = new HashMap<>();
            response.put("adminId", admin.getAdminId());
            response.put("adminname", admin.getAdminname());
            response.put("fullName", admin.getFullName());
            response.put("email", admin.getEmail());
            response.put("idNumber", admin.getIdNumber());
            response.put("sessionId", JSESSIONID_PREFIX + session.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during sign-in.");
        }
    }

    @GetMapping("/validate-session")
    public ResponseEntity<?> validateSession(HttpSession session) {
        AdminEntity admin = (AdminEntity) session.getAttribute(SESSION_USER_KEY);

        if (admin != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("sessionId", JSESSIONID_PREFIX + session.getId());
            response.put("user", admin);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Session is invalid or expired");
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOut(HttpSession session) {
        try {
            session.removeAttribute(SESSION_USER_KEY);
            session.invalidate();
            return ResponseEntity.ok("Signed out successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during sign-out");
        }
    }

    // Create Admin - Protected endpoint
    @PostMapping("/insertAdmin")
public ResponseEntity<?> insertAdmin(@RequestBody AdminEntity admin) {
    // Remove the session check since superuser doesn't need authentication
    admin.setStatus(true); // Default status to active
    return ResponseEntity.ok(adminService.insertAdmin(admin));
}

    // Retrieve All Admins - Protected endpoint
   @GetMapping("/getAllAdmins")
public ResponseEntity<?> getAllAdmins() {
    List<AdminEntity> admins = adminService.getAllAdmins();
    return ResponseEntity.ok(admins);
}

    // Update Password - Protected endpoint
    @PutMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(
            @RequestParam Integer adminId,
            @RequestBody Map<String, String> requestBody,
            HttpSession session) {
        
        // Add debug logging
        System.out.println("Session ID: " + session.getId());
        System.out.println("Admin ID from request: " + adminId);

        try {
            String currentPassword = requestBody.get("currentPassword");
            String newPassword = requestBody.get("newPassword");

            AdminEntity updatedAdmin = adminService.updateAdmin(adminId, newPassword, currentPassword);
            return ResponseEntity.ok(updatedAdmin);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating password: " + e.getMessage());
        }
    }

    // Retrieve Admin by Username - Protected endpoint
    @GetMapping("/getByAdminname")
    public ResponseEntity<?> getAdminByAdminname(
            @RequestParam String adminname,
            HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session invalid or expired");
        }

        try {
            AdminEntity admin = adminService.getAdminByAdminname(adminname);
            return ResponseEntity.ok(admin);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found.");
        }
    }

    // Update Admin Status - Protected endpoint
   @PutMapping("/updateStatus")
public ResponseEntity<?> updateAdminStatus(@RequestBody Map<String, Object> requestBody) {
    String idNumber = (String) requestBody.get("idNumber");
    Boolean status = (Boolean) requestBody.get("status");

    try {
        AdminEntity admin = adminService.getAdminByIdNumber(idNumber);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found.");
        }

        admin.setStatus(status);
        adminService.saveAdmin(admin);

        return ResponseEntity.ok("Status updated successfully.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating admin status.");
    }
}

    // Password reset endpoints don't need session validation
    @PostMapping("/requestPasswordReset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        try {
            String resetCode = adminService.generateResetCode(email);
            return ResponseEntity.ok("Reset code sent to " + email + " with code: " + resetCode);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending reset code.");
        }
    }

    @PostMapping("/verifyResetCode")
    public ResponseEntity<?> verifyResetCode(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String resetCode = requestBody.get("resetCode");

        try {
            adminService.validateResetCode(email, resetCode);
            return ResponseEntity.ok("Reset code verified successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error");
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String newPassword = requestBody.get("newPassword");

        try {
            adminService.resetPassword(email, newPassword);
            return ResponseEntity.ok("Password reset successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error resetting password.");
        }
    }
}
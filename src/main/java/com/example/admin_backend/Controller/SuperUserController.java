package com.example.admin_backend.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.example.admin_backend.Entity.SuperUserEntity;
import com.example.admin_backend.Service.SuperUserService;

@RestController
@RequestMapping("/superuser")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SuperUserController {

    private static final String SESSION_USER_KEY = "superuser";
    private static final String JSESSIONID_PREFIX = "JSESSIONID=";
    private static final int SESSION_TIMEOUT = 30 * 60; // 30 minutes in seconds

    @Autowired
    private SuperUserService superUserService;

    // Helper method to check session
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute(SESSION_USER_KEY) != null;
    }

    @GetMapping("/print")
    public String itWorks() {
        return "It works!";
    }

    // Password Reset Endpoints
    @PostMapping("/requestPasswordReset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> requestBody) {
        try {
            String email = requestBody.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }
            String resetCode = superUserService.generatePasswordResetCode(email);
            return ResponseEntity.ok().body("Reset code sent successfully to " + email);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with email not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending reset code: " + e.getMessage());
        }
    }

    @PostMapping("/validateResetCode")
    public ResponseEntity<?> validateResetCode(@RequestBody Map<String, String> requestBody) {
        try {
            String email = requestBody.get("email");
            String resetCode = requestBody.get("resetCode");
            
            if (email == null || resetCode == null) {
                return ResponseEntity.badRequest().body("Email and reset code are required");
            }
            
            superUserService.validateResetCode(email, resetCode);
            return ResponseEntity.ok("Reset code validated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error validating reset code: " + e.getMessage());
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestBody) {
        try {
            String email = requestBody.get("email");
            String newPassword = requestBody.get("newPassword");
            
            if (email == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Email and new password are required");
            }
            
            superUserService.resetPassword(email, newPassword);
            return ResponseEntity.ok("Password reset successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error resetting password: " + e.getMessage());
        }
    }

    // SuperUser Sign-in with session management
    @PostMapping("/signin")
public ResponseEntity<?> signIn(@RequestBody Map<String, String> loginData, HttpSession session) {
    String superUserIdNumber = loginData.get("superUserIdNumber");
    String superUserPassword = loginData.get("superUserPassword");

    if (superUserIdNumber == null || superUserPassword == null || 
        superUserIdNumber.trim().isEmpty() || superUserPassword.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("ID number and password are required");
    }

    try {
        SuperUserEntity superuser = superUserService.getSuperUserBySuperUserIdNumberAndSuperUserPassword(
                superUserIdNumber.trim(), superUserPassword);
        
        if (superuser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        
        // Move status check before session creation
        if (!superuser.getStatus()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Account is disabled. Please contact the system administrator.");
        }

        // Configure session only if account is enabled
        session.setAttribute(SESSION_USER_KEY, superuser);
        session.setMaxInactiveInterval(SESSION_TIMEOUT);

        Map<String, Object> response = new HashMap<>();
        response.put("superuserId", superuser.getSuperUserId());
        response.put("superUsername", superuser.getSuperUsername());
        response.put("fullName", superuser.getFullName());
        response.put("email", superuser.getEmail());
        response.put("sessionId", JSESSIONID_PREFIX + session.getId());
        
        return ResponseEntity.ok(response);
        
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred during sign-in: " + e.getMessage());
    }
}

    @GetMapping("/validate-session")
    public ResponseEntity<?> validateSession(HttpSession session) {
        SuperUserEntity superuser = (SuperUserEntity) session.getAttribute(SESSION_USER_KEY);

        if (superuser != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("sessionId", JSESSIONID_PREFIX + session.getId());
            response.put("user", superuser);
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

   @PostMapping("/insertSuperUser")
public ResponseEntity<?> insertSuperUser(@RequestBody SuperUserEntity superUser) {
    // Remove session check since we don't need authentication
    
    // Basic validation
    if (superUser.getSuperUsername() == null || superUser.getSuperUsername().isEmpty()) {
        return ResponseEntity.badRequest().body("SuperUsername cannot be null or empty.");
    }

    try {
        // Set default status to true for new accounts
        superUser.setStatus(true);
        
        SuperUserEntity createdUser = superUserService.insertSuperUser(superUser);
        return ResponseEntity.ok(createdUser);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating SuperUser: " + e.getMessage());
    }
}

   @GetMapping("/getAllSuperUsers")
    public ResponseEntity<?> getAllSuperUsers() {
        return ResponseEntity.ok(superUserService.getAllSuperUsers());
    }

    @PutMapping("/updateSuperUserPassword")
    public ResponseEntity<?> updateSuperUserPassword(
            @RequestParam Integer superuserId,
            @RequestBody Map<String, String> requestBody,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session invalid or expired");
        }

        String currentPassword = requestBody.get("currentSuperUserPassword");
        String newPassword = requestBody.get("newSuperUserPassword");

        try {
            SuperUserEntity updatedSuperUser = superUserService.updateSuperUser(superuserId, newPassword, currentPassword);
            return ResponseEntity.ok(updatedSuperUser);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }

    @GetMapping("/getBySuperUsername")
    public ResponseEntity<?> getSuperUserBySuperUsername(
            @RequestParam String superUsername, 
            HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session invalid or expired");
        }

        SuperUserEntity superUser = superUserService.getSuperUserBySuperUsername(superUsername);
        if (superUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(superUser);
    }

   @PutMapping("/updateStatus")
public ResponseEntity<?> updateSuperUserStatus(@RequestBody Map<String, Object> requestBody) {
    String superUserIdNumber = (String) requestBody.get("superUserIdNumber");
    Boolean newStatus = (Boolean) requestBody.get("status");

    try {
        SuperUserEntity updatedSuperUser = superUserService.updateSuperUserStatus(superUserIdNumber, newStatus);
        return ResponseEntity.ok(updatedSuperUser);
    } catch (NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating status.");
    }
}
}
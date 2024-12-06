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

    // SuperUser Sign-in with session management
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody Map<String, String> loginData, HttpSession session) {
        String superUserIdNumber = loginData.get("superUserIdNumber");
        String superUserPassword = loginData.get("superUserPassword");

        System.out.println("Login attempt with ID: " + superUserIdNumber); // Debug log

        if (superUserIdNumber == null || superUserPassword == null) {
            return ResponseEntity.badRequest().body("ID number and password are required");
        }

        try {
            SuperUserEntity superuser = superUserService.getSuperUserBySuperUserIdNumberAndSuperUserPassword(
                    superUserIdNumber, superUserPassword);
            
            if (superuser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            
            if (!superuser.getStatus()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is disabled");
            }

            // Configure session
            session.setAttribute(SESSION_USER_KEY, superuser);
            session.setMaxInactiveInterval(SESSION_TIMEOUT);

            Map<String, Object> response = new HashMap<>();
            response.put("superuserId", superuser.getSuperUserId());
            response.put("superUsername", superuser.getSuperUsername());
            response.put("fullName", superuser.getFullName());
            response.put("email", superuser.getEmail());
            response.put("sessionId", JSESSIONID_PREFIX + session.getId());
            
            return ResponseEntity.ok(response);
            
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
    public ResponseEntity<?> insertSuperUser(@RequestBody SuperUserEntity superUser, HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session invalid or expired");
        }

        if (superUser.getSuperUsername() == null || superUser.getSuperUsername().isEmpty()) {
            return ResponseEntity.badRequest().body("SuperUsername cannot be null or empty.");
        }
        try {
            SuperUserEntity createdUser = superUserService.insertSuperUser(superUser);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating SuperUser: " + e.getMessage());
        }
    }

    @GetMapping("/getAllSuperUsers")
    public ResponseEntity<?> getAllSuperUsers(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session invalid or expired");
        }
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
    public ResponseEntity<?> updateSuperUserStatus(
            @RequestBody Map<String, Object> requestBody,
            HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session invalid or expired");
        }

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
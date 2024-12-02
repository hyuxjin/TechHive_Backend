package com.example.admin_backend.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.admin_backend.Entity.SuperUserEntity;
import com.example.admin_backend.Service.SuperUserService;

@RestController
@RequestMapping("/superuser")
@CrossOrigin(origins = "http://localhost:3000")
public class SuperUserController {

    @Autowired
    private SuperUserService superUserService;

    @GetMapping("/print")
    public String itWorks() {
        return "It works!";
    }

    // Create SuperUser
    @PostMapping("/insertSuperUser")
    public ResponseEntity<?> insertSuperUser(@RequestBody SuperUserEntity superUser) {
        if (superUser.getSuperUsername() == null || superUser.getSuperUsername().isEmpty()) {
            return ResponseEntity.badRequest().body("SuperUsername cannot be null or empty.");
        }
        try {
            SuperUserEntity createdUser = superUserService.insertSuperUser(superUser);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating SuperUser: " + e.getMessage());
        }
    }

    // Request Password Reset
    @PostMapping("/requestPasswordReset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");

        try {
            String resetCode = superUserService.generatePasswordResetCode(email);
            return ResponseEntity.ok("Password reset code sent successfully to " + email);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while sending password reset code.");
        }
    }

    // Verify Reset Code
    @PostMapping("/verifyResetCode")
    public ResponseEntity<?> verifyResetCode(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String resetCode = requestBody.get("resetCode");

        try {
            superUserService.validateResetCode(email, resetCode);
            return ResponseEntity.ok("Reset code verified successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying reset code.");
        }
    }

    // Reset Password
    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String newPassword = requestBody.get("newPassword");

        try {
            superUserService.resetPassword(email, newPassword);
            return ResponseEntity.ok("Password reset successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error resetting password.");
        }
    }

    // Read All SuperUsers
    @GetMapping("/getAllSuperUsers")
    public ResponseEntity<List<SuperUserEntity>> getAllSuperUsers() {
        return ResponseEntity.ok(superUserService.getAllSuperUsers());
    }

    // Update SuperUser Password
    @PutMapping("/updateSuperUserPassword")
    public ResponseEntity<?> updateSuperUserPassword(
            @RequestParam Integer superuserId,
            @RequestBody Map<String, String> requestBody) {
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

    // SuperUser Sign-in
    @PostMapping("/signin")
public ResponseEntity<?> signIn(@RequestBody Map<String, String> loginData) {
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

        Map<String, Object> response = new HashMap<>();
        response.put("token", "dummyToken");
        response.put("superuserId", superuser.getSuperUserId());
        response.put("superUsername", superuser.getSuperUsername());
        response.put("fullName", superuser.getFullName());
        response.put("email", superuser.getEmail());
        
        return ResponseEntity.ok(response);
        
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid credentials: " + e.getMessage());
    } catch (Exception e) {
        e.printStackTrace(); // This will print to server logs
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred during sign-in: " + e.getMessage());
    }
}

    // Get SuperUser by Username
    @GetMapping("/getBySuperUsername")
    public ResponseEntity<SuperUserEntity> getSuperUserBySuperUsername(@RequestParam String superUsername) {
        SuperUserEntity superUser = superUserService.getSuperUserBySuperUsername(superUsername);
        if (superUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(superUser);
    }

    // Update SuperUser Status
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

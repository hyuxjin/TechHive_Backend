package com.example.admin_backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.admin_backend.Entity.UserEntity;
import com.example.admin_backend.Service.UserService;
import com.example.admin_backend.dto.UserDTO;
import jakarta.servlet.http.HttpSession;

import com.example.admin_backend.Service.EmailVerificationService;
import com.example.admin_backend.Service.PasswordResetServiceUser;

import java.util.NoSuchElementException;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetServiceUser passwordResetServiceUser;

    @Autowired
    private EmailVerificationService emailVerificationService; 

    // Create new user
    @PostMapping("/insertUser")
    public ResponseEntity<UserEntity> insertUser(@RequestBody UserEntity user) {
        return ResponseEntity.ok(userService.insertUser(user));
    }

    // Get all users
    @GetMapping("/getAllUsers")
    public ResponseEntity<Iterable<UserEntity>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Get user by username
    @GetMapping("/getByUsername")
    public ResponseEntity<UserEntity> getUserByUsername(@RequestParam String username) {
        UserEntity user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/validate-session")
    public ResponseEntity<?> validateSession(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");

        if (user != null) {
            return ResponseEntity.ok("Session is valid for user: " + user.getFullName());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session is invalid or expired");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpSession session, @RequestBody Map<String, String> loginDetails) {
        String idNumber = loginDetails.get("idNumber");
        String password = loginDetails.get("password");

        try {
            UserEntity user = userService.authenticateUser(idNumber, password);

            // Store user info in session
            session.setAttribute("user", user);

            // Return sanitized user details
            UserDTO userDTO = new UserDTO(user);
            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }

    // Update user password
    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUserPassword(@RequestParam Integer userId,
                                                @RequestBody Map<String, String> requestBody) {
        String currentPassword = requestBody.get("currentPassword");
        String newPassword = requestBody.get("newPassword");

        // Check for null or empty passwords
        if (currentPassword == null || newPassword == null || currentPassword.isEmpty() || newPassword.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Current or new password cannot be null or empty.");
        }

        try {
            UserEntity updatedUser = userService.updateUserPassword(userId, newPassword, currentPassword);
            return ResponseEntity.ok(updatedUser);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Request password reset
    @PostMapping("/requestPasswordReset")
    public ResponseEntity<String> requestPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        try {
            String resetCode = passwordResetServiceUser.generateResetCode();
            passwordResetServiceUser.sendResetCode(email, resetCode);
            return ResponseEntity.ok("Password reset email sent.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send email: " + e.getMessage());
        }
    }

    // Verify reset code
    @PostMapping("/verifyResetCode")
    public ResponseEntity<String> verifyResetCode(@RequestParam String email, @RequestParam String resetCode) {
        boolean isValid = passwordResetServiceUser.verifyResetCode(email, resetCode);
        if (isValid) {
            return ResponseEntity.ok("Verification successful.");
        } else {
            return ResponseEntity.badRequest().body("Invalid verification code or user not found.");
        }
    }

    // Reset password
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Passwords do not match.");
        }

        try {
            passwordResetServiceUser.resetPassword(email, newPassword);
            return ResponseEntity.ok("Password reset successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Failed to reset password: " + e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("User not found.");
        }
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<?> updateUserStatus(@RequestBody Map<String, Object> requestBody) {
        try {
            String idNumber = (String) requestBody.get("idNumber");
            boolean status = (boolean) requestBody.get("status");
            
            UserEntity updatedUser = userService.updateUserStatus(idNumber, status);
            return ResponseEntity.ok(updatedUser);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                               .body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Error updating user status: " + e.getMessage());
        }
    }

    // Send email verification code
    @PostMapping("/sendVerificationEmail")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    String fullName = request.get("fullName");

    try {
        emailVerificationService.sendVerificationCode(email, fullName);
        return ResponseEntity.ok("Verification email sent successfully.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send verification email.");
    }
}


    // Verify email verification code
    @PostMapping("/verifyCode")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
    
        boolean isValid = userService.verifyEmailCode(email, code);
        if (isValid) {
            return ResponseEntity.ok("Email verified successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired code.");
        }
    }
    

}

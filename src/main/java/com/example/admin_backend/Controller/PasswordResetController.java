package com.example.admin_backend.Controller;

import com.example.admin_backend.Service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/password-reset")
@CrossOrigin(origins = "http://localhost:3000")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/request")
    public ResponseEntity<?> requestResetCode(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String userType = requestBody.get("userType");

        try {
            passwordResetService.generateResetCode(email, userType);
            return ResponseEntity.ok("Reset code sent to " + email);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyResetCode(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String resetCode = requestBody.get("resetCode");
        String userType = requestBody.get("userType");

        try {
            passwordResetService.validateResetCode(email, resetCode, userType);
            return ResponseEntity.ok("Reset code verified successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String newPassword = requestBody.get("newPassword");
        String userType = requestBody.get("userType");

        try {
            passwordResetService.resetPassword(email, newPassword, userType);
            return ResponseEntity.ok("Password reset successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

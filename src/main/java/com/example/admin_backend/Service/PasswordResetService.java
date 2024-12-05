package com.example.admin_backend.Service;

import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Entity.SuperUserEntity;
import com.example.admin_backend.Repository.AdminRepository;
import com.example.admin_backend.Repository.PasswordResetRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EmailService emailService;

    // Generate reset code and send email
    public void generateResetCode(String email, String userType) throws MessagingException {
        if (userType.equalsIgnoreCase("superuser")) {
            SuperUserEntity superUser = passwordResetRepository.findByEmail(email)
                    .orElseThrow(() -> new NoSuchElementException("SuperUser with email " + email + " not found."));

            String resetCode = generateRandomCode();
            superUser.setResetCode(resetCode);
            superUser.setResetCodeTimestamp(LocalDateTime.now());
            passwordResetRepository.save(superUser);

            sendResetEmail(email, resetCode);
        } else if (userType.equalsIgnoreCase("admin")) {
            AdminEntity admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new NoSuchElementException("Admin with email " + email + " not found."));

            String resetCode = generateRandomCode();
            admin.setResetCode(resetCode);
            admin.setResetCodeTimestamp(LocalDateTime.now());
            adminRepository.save(admin);

            sendResetEmail(email, resetCode);
        } else {
            throw new IllegalArgumentException("Invalid user type: " + userType);
        }
    }

    // Validate reset code
    public void validateResetCode(String email, String resetCode, String userType) {
        if (userType.equalsIgnoreCase("superuser")) {
            SuperUserEntity superUser = passwordResetRepository.findByEmailAndResetCode(email, resetCode)
                    .orElseThrow(() -> new NoSuchElementException("Invalid reset code or email for SuperUser."));

            if (isCodeExpired(superUser.getResetCodeTimestamp())) {
                throw new IllegalArgumentException("Reset code has expired.");
            }
        } else if (userType.equalsIgnoreCase("admin")) {
            AdminEntity admin = adminRepository.findByEmailAndResetCode(email, resetCode)
                    .orElseThrow(() -> new NoSuchElementException("Invalid reset code or email for Admin."));

            if (isCodeExpired(admin.getResetCodeTimestamp())) {
                throw new IllegalArgumentException("Reset code has expired.");
            }
        } else {
            throw new IllegalArgumentException("Invalid user type: " + userType);
        }
    }

    // Reset password
    public void resetPassword(String email, String newPassword, String userType) {
        if (userType.equalsIgnoreCase("superuser")) {
            SuperUserEntity superUser = passwordResetRepository.findByEmail(email)
                    .orElseThrow(() -> new NoSuchElementException("SuperUser with email " + email + " not found."));

            superUser.setSuperUserPassword(newPassword);
            superUser.setResetCode(null);
            superUser.setResetCodeTimestamp(null);
            passwordResetRepository.save(superUser);
        } else if (userType.equalsIgnoreCase("admin")) {
            AdminEntity admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new NoSuchElementException("Admin with email " + email + " not found."));

            admin.setPassword(newPassword);
            admin.setResetCode(null);
            admin.setResetCodeTimestamp(null);
            adminRepository.save(admin);
        } else {
            throw new IllegalArgumentException("Invalid user type: " + userType);
        }
    }

    // Helper: Send reset email
    private void sendResetEmail(String email, String resetCode) throws MessagingException {
        String subject = "Password Reset Request";
        String body = "Your password reset code is: " + resetCode + "\n\nThis code will expire in 10 minutes.";
        emailService.sendEmail(email, subject, body);
    }

    // Helper: Generate random reset code
    private String generateRandomCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000); // 6-digit code
    }

    // Helper: Check if reset code is expired
    private boolean isCodeExpired(LocalDateTime timestamp) {
        return timestamp == null || timestamp.isBefore(LocalDateTime.now().minusMinutes(10));
    }
}

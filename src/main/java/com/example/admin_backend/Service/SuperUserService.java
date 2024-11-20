package com.example.admin_backend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.admin_backend.Entity.SuperUserEntity;
import com.example.admin_backend.Repository.SuperUserRepository;

@Service
public class SuperUserService {

    @Autowired
    private SuperUserRepository superUserRepository;

    @Autowired
    private EmailService emailService; // For sending email notifications

    // Create or insert a SuperUser
    public SuperUserEntity insertSuperUser(SuperUserEntity superuser) {
        return superUserRepository.save(superuser);
    }

    // Retrieve all SuperUsers
    public List<SuperUserEntity> getAllSuperUsers() {
        return superUserRepository.findAll();
    }

    // Update SuperUser password
    public SuperUserEntity updateSuperUser(int superuserId, String newPassword, String currentPassword) {
        SuperUserEntity superuser = superUserRepository.findById(superuserId)
                .orElseThrow(() -> new NoSuchElementException("SuperUser not found"));

        // Validate current password
        if (!superuser.getSuperUserPassword().equals(currentPassword)) {
            throw new IllegalArgumentException("Incorrect current password.");
        }

        // Update password
        superuser.setSuperUserPassword(newPassword);
        return superUserRepository.save(superuser);
    }

    // Retrieve SuperUser by username
    public SuperUserEntity getSuperUserBySuperUsername(String superUsername) {
        return superUserRepository.findBySuperUsername(superUsername)
                .orElseThrow(() -> new NoSuchElementException("SuperUser not found"));
    }

    // Sign-in SuperUser
    public SuperUserEntity getSuperUserBySuperUserIdNumberAndSuperUserPassword(String superUserIdNumber, String password) {
        Optional<SuperUserEntity> optionalSuperuser = superUserRepository.findBySuperUserIdNumber(superUserIdNumber);

        if (optionalSuperuser.isPresent() && optionalSuperuser.get().getSuperUserPassword().equals(password)) {
            return optionalSuperuser.get();
        }

        throw new IllegalArgumentException("Invalid credentials");
    }

    // Update SuperUser status
    public SuperUserEntity updateSuperUserStatus(String superUserIdNumber, boolean newStatus) {
        SuperUserEntity superuser = superUserRepository.findBySuperUserIdNumber(superUserIdNumber)
                .orElseThrow(() -> new NoSuchElementException("SuperUser not found"));

        superuser.setStatus(newStatus);
        return superUserRepository.save(superuser);
    }

    // Generate and send password reset code
    public String generatePasswordResetCode(String email) {
        SuperUserEntity superuser = superUserRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("SuperUser with email not found"));

        // Generate a secure random reset code
        String resetCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        superuser.setResetCode(resetCode);
        superuser.setResetCodeTimestamp(LocalDateTime.now());
        superuser.setResetCodeVerified(false); // Reset the verification flag
        superUserRepository.save(superuser);

        // Send reset code via email
        String subject = "Password Reset Code";
        String message = "Your password reset code is: " + resetCode;

        try {
            emailService.sendEmail(email, subject, message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }

        return resetCode; // Return for testing/logging purposes
    }

    // Verify the reset code
    public void validateResetCode(String email, String resetCode) {
        SuperUserEntity superuser = superUserRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("SuperUser with email not found"));

        if (superuser.getResetCode() == null || !superuser.getResetCode().equals(resetCode)) {
            throw new IllegalArgumentException("Invalid reset code.");
        }

        if (superuser.getResetCodeTimestamp() == null || 
            superuser.getResetCodeTimestamp().isBefore(LocalDateTime.now().minusMinutes(10))) {
            throw new IllegalArgumentException("Reset code expired. Please request a new one.");
        }

        // Mark reset code as validated
        superuser.setResetCodeVerified(true);
        superUserRepository.save(superuser);
    }

    // Reset password
    public void resetPassword(String email, String newPassword) {
        SuperUserEntity superuser = superUserRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("SuperUser with email not found"));

        // Ensure reset code is verified
        if (superuser.getResetCodeVerified() == null || !superuser.getResetCodeVerified()) {
            throw new IllegalArgumentException("Reset code not verified.");
        }

        // Reset password and clear reset code data
        superuser.setSuperUserPassword(newPassword);
        superuser.setResetCode(null);
        superuser.setResetCodeTimestamp(null);
        superuser.setResetCodeVerified(false); // Clear the verified status
        superUserRepository.save(superuser);
    }
}

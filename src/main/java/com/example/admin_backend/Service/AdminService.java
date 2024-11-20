package com.example.admin_backend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Repository.AdminRepository;

import jakarta.mail.MessagingException;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EmailService emailService; // Email service for sending reset codes

    // Create or insert admin record in tblAdmins
    public AdminEntity insertAdmin(AdminEntity admin) {
        return adminRepository.save(admin);
    }

    // Read all records in tblAdmins
    public List<AdminEntity> getAllAdmins() {
        return adminRepository.findAll();
    }

    // Update an admin
    public AdminEntity updateAdmin(int adminId, String newPassword, String currentPassword) {
        AdminEntity admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new NoSuchElementException("Admin " + adminId + " not found"));

        // Validate the current password
        if (!admin.getPassword().equals(currentPassword)) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        // Update the admin details
        admin.setPassword(newPassword);
        return adminRepository.save(admin);
    }

    // Get admin by adminname
    public AdminEntity getAdminByAdminname(String adminname) {
        return adminRepository.findByAdminname(adminname);
    }

    // Get admin by ID number and password
    public AdminEntity getAdminByIdNumberAndPassword(String idNumber, String password) {
        AdminEntity admin = adminRepository.findByIdNumber(idNumber);
        if (admin != null && admin.getPassword().equals(password)) {
            return admin;
        }
        return null;
    }

    // Get admin by ID
    public AdminEntity getAdminByIdNumber(String idNumber) {
        return adminRepository.findByIdNumber(idNumber);
    }

    // Save or update an admin
    public AdminEntity saveAdmin(AdminEntity admin) {
        return adminRepository.save(admin);
    }

    // Generate reset code
    public String generateResetCode(String email) throws MessagingException {
        AdminEntity admin = adminRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NoSuchElementException("Admin with email " + email + " not found."));

        // Generate 6-digit reset code
        String resetCode = String.valueOf((int) (Math.random() * 900000) + 100000);
        admin.setResetCode(resetCode);
        admin.setResetCodeTimestamp(LocalDateTime.now());
        admin.setResetCodeVerified(false); // Set verified flag to false
        adminRepository.save(admin);

        // Send reset code via email
        String subject = "Password Reset Code";
        String body = "Your password reset code is: " + resetCode + "\n\nThis code will expire in 10 minutes.";
        emailService.sendEmail(email, subject, body);

        return resetCode;
    }

    // Validate reset code
    public void validateResetCode(String email, String resetCode) {
        AdminEntity admin = adminRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NoSuchElementException("Admin with email " + email + " not found."));

        if (!resetCode.equals(admin.getResetCode())) {
            throw new IllegalArgumentException("Invalid reset code.");
        }

        if (admin.getResetCodeTimestamp() == null ||
            admin.getResetCodeTimestamp().isBefore(LocalDateTime.now().minusMinutes(10))) {
            throw new IllegalArgumentException("Reset code has expired.");
        }

        // Mark reset code as verified
        admin.setResetCodeVerified(true);
        adminRepository.save(admin);
    }

    // Reset password
    public void resetPassword(String email, String newPassword) {
        AdminEntity admin = adminRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NoSuchElementException("Admin with email " + email + " not found."));

        if (admin.getResetCodeVerified() == null || !admin.getResetCodeVerified()) {
            throw new IllegalArgumentException("Reset code has not been verified.");
        }

        // Update password and clear reset-related fields
        admin.setPassword(newPassword);
        admin.setResetCode(null);
        admin.setResetCodeTimestamp(null);
        admin.setResetCodeVerified(false);

        adminRepository.save(admin);
    }
}

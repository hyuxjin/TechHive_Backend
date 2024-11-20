package com.example.admin_backend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Repository.AdminRepository;

<<<<<<< HEAD
import jakarta.mail.MessagingException;
=======
import jakarta.annotation.PostConstruct;
>>>>>>> parent of f6a8264 (Revert "Implement password hashing for Admin and SuperUser")

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;
    
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

<<<<<<< HEAD
    @Autowired
    private EmailService emailService; // Email service for sending reset codes

    // Create or insert admin record in tblAdmins
=======
    @PostConstruct
    public void init() {
    hashExistingPasswords();
    }
    
    // Method to hash existing passwords
    public void hashExistingPasswords() {
    List<AdminEntity> admins = adminRepository.findAll();
    
    for(AdminEntity admin : admins) {
    // Only hash if password isn't already hashed
        if(!isPasswordHashed(admin.getPassword())) {
            admin.setPassword(encoder.encode(admin.getPassword()));
            adminRepository.save(admin);
        }
    }
}

    // Helper method to check if password is already hashed
    private boolean isPasswordHashed(String password) {
    return password.startsWith("$2a$");
}
    
>>>>>>> parent of f6a8264 (Revert "Implement password hashing for Admin and SuperUser")
    public AdminEntity insertAdmin(AdminEntity admin) {
        admin.setPassword(encoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }
    
    public List<AdminEntity> getAllAdmins() {
        return adminRepository.findAll();
    }
    
    public AdminEntity updateAdmin(int adminId, String newPassword, String currentPassword) {
        AdminEntity admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new NoSuchElementException("Admin not found"));
        if (!encoder.matches(currentPassword, admin.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
<<<<<<< HEAD

        // Update the admin details
        admin.setPassword(newPassword);
=======
        admin.setPassword(encoder.encode(newPassword));
>>>>>>> parent of f6a8264 (Revert "Implement password hashing for Admin and SuperUser")
        return adminRepository.save(admin);
    }
    
    public AdminEntity getAdminByAdminname(String adminname) {
        return adminRepository.findByAdminname(adminname);
    }
    
    public AdminEntity getAdminByIdNumberAndPassword(String idNumber, String password) {
        AdminEntity admin = adminRepository.findByIdNumber(idNumber);
        if (admin != null && encoder.matches(password, admin.getPassword())) {
            return admin;
        }
        return null;
    }
<<<<<<< HEAD

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
=======
    
    public AdminEntity getAdminByIdNumber(String idNumber) {
        return adminRepository.findByIdNumber(idNumber);
    }
    
    public AdminEntity saveAdmin(AdminEntity admin) {
        return adminRepository.save(admin);
    }
}

>>>>>>> parent of f6a8264 (Revert "Implement password hashing for Admin and SuperUser")

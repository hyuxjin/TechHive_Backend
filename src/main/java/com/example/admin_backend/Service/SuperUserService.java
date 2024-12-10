package com.example.admin_backend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.example.admin_backend.Entity.SuperUserEntity;
import com.example.admin_backend.Repository.SuperUserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class SuperUserService {

    @Autowired
    private SuperUserRepository superUserRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private EmailService emailService;

    @PostConstruct
    public void init() {
        hashExistingPasswords();
    }
    
    // Hash existing passwords
    @Transactional
    public void hashExistingPasswords() {
        List<SuperUserEntity> superusers = superUserRepository.findAll();
        for(SuperUserEntity superuser : superusers) {
            if(!isPasswordHashed(superuser.getSuperUserPassword())) {
                superuser.setSuperUserPassword(encoder.encode(superuser.getSuperUserPassword()));
                superUserRepository.save(superuser);
            }
        }
    }
    
    private boolean isPasswordHashed(String password) {
        return password != null && password.startsWith("$2a$");
    }

    // Create new SuperUser with hashed password
   @Transactional
public SuperUserEntity insertSuperUser(SuperUserEntity superuser) {
    // Encrypt the password before saving
    superuser.setSuperUserPassword(encoder.encode(superuser.getSuperUserPassword()));
    return superUserRepository.save(superuser);
}

    public List<SuperUserEntity> getAllSuperUsers() {
        return superUserRepository.findAll();
    }

    // Update password with hashing
    @Transactional
    public SuperUserEntity updateSuperUser(int superuserId, String newPassword, String currentPassword) {
        SuperUserEntity superuser = superUserRepository.findById(superuserId)
                .orElseThrow(() -> new NoSuchElementException("SuperUser not found"));

        if (!encoder.matches(currentPassword, superuser.getSuperUserPassword())) {
            throw new IllegalArgumentException("Incorrect current password.");
        }

        superuser.setSuperUserPassword(encoder.encode(newPassword));
        return superUserRepository.save(superuser);
    }

// Update method name to match repository
public SuperUserEntity getSuperUserBySuperUsername(String superUsername) {
    return superUserRepository.findBySuperUsername(superUsername)
            .orElseThrow(() -> new NoSuchElementException("SuperUser not found"));
}

// Update method name and parameter to match repository
public SuperUserEntity getSuperUserBySuperUserIdNumberAndSuperUserPassword(String superUserIdNumber, String password) {
    Optional<SuperUserEntity> optionalSuperuser = superUserRepository.findBySuperUserIdNumber(superUserIdNumber);

    if (optionalSuperuser.isPresent() && 
        encoder.matches(password, optionalSuperuser.get().getSuperUserPassword())) {
        return optionalSuperuser.get();
    }
    throw new IllegalArgumentException("Invalid credentials");
}

// Update method name to match repository
public SuperUserEntity updateSuperUserStatus(String superUserIdNumber, boolean newStatus) {
    SuperUserEntity superuser = superUserRepository.findBySuperUserIdNumber(superUserIdNumber)
            .orElseThrow(() -> new NoSuchElementException("SuperUser not found"));

    superuser.setStatus(newStatus);
    return superUserRepository.save(superuser);
}

    // Password reset functionality with hashing
    @Transactional
    public String generatePasswordResetCode(String email) {
        SuperUserEntity superuser = superUserRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("SuperUser with email not found"));

        String resetCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        superuser.setResetCode(resetCode);
        superuser.setResetCodeTimestamp(LocalDateTime.now());
        superuser.setResetCodeVerified(false);
        superUserRepository.save(superuser);

        String subject = "Password Reset Code";
        String message = "Your password reset code is: " + resetCode;

        try {
            emailService.sendEmail(email, subject, message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }

        return resetCode;
    }

    @Transactional
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

        superuser.setResetCodeVerified(true);
        superUserRepository.save(superuser);
    }

    // Reset password with hashing
    @Transactional
    public void resetPassword(String email, String newPassword) {
        SuperUserEntity superuser = superUserRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("SuperUser with email not found"));

        if (superuser.getResetCodeVerified() == null || !superuser.getResetCodeVerified()) {
            throw new IllegalArgumentException("Reset code not verified.");
        }

        superuser.setSuperUserPassword(encoder.encode(newPassword));
        superuser.setResetCode(null);
        superuser.setResetCodeTimestamp(null);
        superuser.setResetCodeVerified(false);
        superUserRepository.save(superuser);
    }
}
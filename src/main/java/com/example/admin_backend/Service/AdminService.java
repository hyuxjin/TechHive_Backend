package com.example.admin_backend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Repository.AdminRepository;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;

@Service
public class AdminService {

   @Autowired
   private AdminRepository adminRepository;

   private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

   @Autowired
   private EmailService emailService;

   @PostConstruct
   public void init() {
       hashExistingPasswords();
   }
   
   @Transactional
   public void hashExistingPasswords() {
       List<AdminEntity> admins = adminRepository.findAll();
       for(AdminEntity admin : admins) {
           if(!isPasswordHashed(admin.getPassword())) {
               admin.setPassword(encoder.encode(admin.getPassword()));
               adminRepository.save(admin);
           }
       }
   }
   
   private boolean isPasswordHashed(String password) {
       return password != null && password.startsWith("$2a$");
   }

   @Transactional
   public AdminEntity insertAdmin(AdminEntity admin) {
       admin.setPassword(encoder.encode(admin.getPassword()));
       return adminRepository.save(admin);
   }

   public List<AdminEntity> getAllAdmins() {
       return adminRepository.findAll();
   }

   @Transactional
   public AdminEntity updateAdmin(int adminId, String newPassword, String currentPassword) {
       AdminEntity admin = adminRepository.findById(adminId)
               .orElseThrow(() -> new NoSuchElementException("Admin " + adminId + " not found"));

       if (!encoder.matches(currentPassword, admin.getPassword())) {
           throw new IllegalArgumentException("Current password is incorrect.");
       }

       admin.setPassword(encoder.encode(newPassword));
       return adminRepository.save(admin);
   }

   public AdminEntity getAdminByAdminname(String adminname) {
       return adminRepository.findByAdminname(adminname);
   }

   public AdminEntity getAdminByIdNumberAndPassword(String idNumber, String password) {
    AdminEntity admin = adminRepository.findByIdNumber(idNumber);
    if (admin != null && encoder.matches(password, admin.getPassword())) {
        // Only allow login if account is enabled
        if (!admin.getStatus()) {
            return null; // Return null for disabled accounts
        }
        return admin;
    }
    return null;
}

   public AdminEntity getAdminByIdNumber(String idNumber) {
       return adminRepository.findByIdNumber(idNumber);
   }

   @Transactional
   public AdminEntity saveAdmin(AdminEntity admin) {
       if (!isPasswordHashed(admin.getPassword())) {
           admin.setPassword(encoder.encode(admin.getPassword()));
       }
       return adminRepository.save(admin);
   }

   @Transactional
   public String generateResetCode(String email) throws MessagingException {
       AdminEntity admin = adminRepository.findByEmailIgnoreCase(email)
               .orElseThrow(() -> new NoSuchElementException("Admin with email " + email + " not found."));

       String resetCode = String.valueOf((int) (Math.random() * 900000) + 100000);
       admin.setResetCode(resetCode);
       admin.setResetCodeTimestamp(LocalDateTime.now());
       admin.setResetCodeVerified(false);
       adminRepository.save(admin);

       String subject = "Password Reset Code";
       String body = "Your password reset code is: " + resetCode + "\n\nThis code will expire in 10 minutes.";
       emailService.sendEmail(email, subject, body);

       return resetCode;
   }

   @Transactional
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

       admin.setResetCodeVerified(true);
       adminRepository.save(admin);
   }

   @Transactional
   public void resetPassword(String email, String newPassword) {
       AdminEntity admin = adminRepository.findByEmailIgnoreCase(email)
               .orElseThrow(() -> new NoSuchElementException("Admin with email " + email + " not found."));

       if (admin.getResetCodeVerified() == null || !admin.getResetCodeVerified()) {
           throw new IllegalArgumentException("Reset code has not been verified.");
       }

       admin.setPassword(encoder.encode(newPassword));
       admin.setResetCode(null);
       admin.setResetCodeTimestamp(null);
       admin.setResetCodeVerified(false);

       adminRepository.save(admin);
   }
}
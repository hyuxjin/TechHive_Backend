package com.example.admin_backend.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Repository.AdminRepository;

import jakarta.annotation.PostConstruct;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;
    
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
        admin.setPassword(encoder.encode(newPassword));
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
    
    public AdminEntity getAdminByIdNumber(String idNumber) {
        return adminRepository.findByIdNumber(idNumber);
    }
    
    public AdminEntity saveAdmin(AdminEntity admin) {
        return adminRepository.save(admin);
    }
}


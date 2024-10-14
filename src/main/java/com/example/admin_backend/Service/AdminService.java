package com.example.admin_backend.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Repository.AdminRepository;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

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

}

package com.example.admin_backend.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Service.AdminService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/print")
    public String itWorks() {
        return "It works";
    }

    // Create
    @PostMapping("/insertAdmin")
    public AdminEntity insertAdmin(@RequestBody AdminEntity admin) {
        if (admin.getStatus()) {
            admin.setStatus(true);
        }
        return adminService.insertAdmin(admin);
    }

    // Read
    @GetMapping("/getAllAdmins")
    public List<AdminEntity> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    // Update an admin record
    @PutMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestParam Integer adminId, 
                                            @RequestBody Map<String, String> requestBody) {
        String currentPassword = requestBody.get("currentPassword");
        String newPassword = requestBody.get("newPassword");

        try {
            AdminEntity updatedAdmin = adminService.updateAdmin(adminId, newPassword, currentPassword);
            return ResponseEntity.ok(updatedAdmin);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    // Sign-in
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody Map<String, String> loginData) {
        String idNumber = loginData.get("idNumber"); // Changed from email to idNumber
        String password = loginData.get("password");

        try {
            AdminEntity admin = adminService.getAdminByIdNumberAndPassword(idNumber, password); // Using idNumber instead of email
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID Number or password.");
            }

            if (!admin.getStatus()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is disabled.");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("token", "dummyToken"); // Replace with actual token if using JWT
            response.put("adminId", admin.getAdminId());
            response.put("adminname", admin.getAdminname());
            response.put("fullName", admin.getFullName());
            response.put("email", admin.getEmail());
            response.put("idNumber", admin.getIdNumber());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during sign-in.");
        }
    }

    // Get admin by ID number
    @GetMapping("/getByAdminname")
    public ResponseEntity<AdminEntity> getAdminByAdminname(@RequestParam String adminname) {
        AdminEntity admin = adminService.getAdminByAdminname(adminname);
        if (admin == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(admin);
    }

       @PutMapping("/updateStatus")
public ResponseEntity<?> updateAdminStatus(@RequestBody Map<String, Object> requestBody) {
    String idNumber = (String) requestBody.get("idNumber");
    Boolean status = (Boolean) requestBody.get("status");

    try {
        AdminEntity admin = adminService.getAdminByIdNumber(idNumber); // Use your existing service method to get the admin by ID
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found.");
        }

        // Update the admin's status
        admin.setStatus(status);
        adminService.saveAdmin(admin);  // Save the updated admin entity

        return ResponseEntity.ok("Status updated successfully.");  // Return the updated admin
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating admin status.");
    }
}
}

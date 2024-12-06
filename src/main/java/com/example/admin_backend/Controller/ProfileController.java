package com.example.admin_backend.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.admin_backend.Entity.ProfileEntity;
import com.example.admin_backend.Entity.UserEntity;
import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Repository.ProfileRepository;
import com.example.admin_backend.Repository.UserRepository;
import com.example.admin_backend.Repository.AdminRepository;
import com.example.admin_backend.Service.ProfileService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/profile")
public class ProfileController {
    
    @Autowired
    private ProfileService profileService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private ProfileRepository profileRepository;

    // Upload Profile Picture for both User and Admin
    @PostMapping("/{role}/uploadProfilePicture")
    public ResponseEntity<?> uploadProfilePicture(
            @PathVariable String role,
            @RequestParam("id") int id,
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File cannot be empty");
        }

        try {
            byte[] profilePicture = file.getBytes();
            ProfileEntity savedProfile;
            
            if ("user".equalsIgnoreCase(role)) {
                savedProfile = profileService.saveUserProfilePicture(id, profilePicture);
            } else if ("admin".equalsIgnoreCase(role)) {
                savedProfile = profileService.saveAdminProfilePicture(id, profilePicture);
            } else {
                return ResponseEntity.badRequest().body("Invalid role specified");
            }
            
            return ResponseEntity.ok(savedProfile);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to process file: " + e.getMessage());
        }
    }

    // Get Profile Picture for both User and Admin
    @GetMapping("/{role}/getProfilePicture/{id}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String role, @PathVariable int id) {
        byte[] profilePicture = null;
        
        if ("user".equalsIgnoreCase(role)) {
            UserEntity user = userRepository.findById(id).orElse(null);
            if (user != null) {
                ProfileEntity profile = profileRepository.findByUser(user);
                if (profile != null && profile.getProfilePicture() != null) {
                    profilePicture = profile.getProfilePicture();
                }
            }
        } else if ("admin".equalsIgnoreCase(role)) {
            AdminEntity admin = adminRepository.findById(id).orElse(null);
            if (admin != null) {
                ProfileEntity profile = profileRepository.findByAdmin(admin);
                if (profile != null && profile.getProfilePicture() != null) {
                    profilePicture = profile.getProfilePicture();
                }
            }
        }

        // If no profile picture found, return default image from static resources
        if (profilePicture == null) {
            try {
                Path path = Paths.get("src/main/resources/static/default.png");  // Default image path
                profilePicture = Files.readAllBytes(path);
            } catch (IOException e) {
                return ResponseEntity.internalServerError().build();
            }
        }

        return ResponseEntity.ok(profilePicture);
    }

    // Delete Profile Picture
    @PostMapping("/{role}/deleteProfilePicture/{id}")
    public ResponseEntity<String> deleteProfilePicture(@PathVariable String role, @PathVariable int id) {
        try {
            if ("user".equalsIgnoreCase(role)) {
                profileService.deleteUserProfilePicture(id);
            } else if ("admin".equalsIgnoreCase(role)) {
                profileService.deleteAdminProfilePicture(id);
            } else {
                return ResponseEntity.badRequest().body("Invalid role specified");
            }
            
            return ResponseEntity.ok("Profile picture deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting profile picture: " + e.getMessage());
        }
    }
}

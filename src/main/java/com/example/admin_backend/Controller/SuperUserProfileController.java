package com.example.admin_backend.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.admin_backend.Entity.SuperUserProfileEntity;
import com.example.admin_backend.Entity.SuperUserEntity;
import com.example.admin_backend.Repository.SuperUserProfileRepository;
import com.example.admin_backend.Repository.SuperUserRepository;
import com.example.admin_backend.Service.SuperUserProfileService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/superuser/profile")
public class SuperUserProfileController {

    @Autowired
    private SuperUserProfileService superUserProfileService;

    @Autowired
    private SuperUserRepository superUserRepository;

    @Autowired
    private SuperUserProfileRepository superUserProfileRepository;

    @PostMapping("/uploadProfilePicture")
    public ResponseEntity<?> uploadSuperUserProfilePicture(
            @RequestParam("superuserId") int superuserId,
            @RequestParam("file") MultipartFile file) {
        System.out.println("Received upload request for SuperUser ID: " + superuserId); // Debug log
    
        Optional<SuperUserEntity> superUserOptional = superUserRepository.findById(superuserId);
        if (!superUserOptional.isPresent()) {
            return ResponseEntity.badRequest().body("Superuser not found.");
        }
    
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty or not provided.");
        }
        try {
            if (file.getSize() > 2 * 1024 * 1024) { // Max size of 2MB
                return ResponseEntity.badRequest().body("File size exceeds the 2MB limit.");
            }
    
            byte[] profilePicture = file.getBytes();
            SuperUserProfileEntity updatedProfile = superUserProfileService.saveSuperUserProfilePicture(superuserId, profilePicture);
    
            // Return the profile ID after saving the picture
            return ResponseEntity.ok("Profile picture uploaded successfully. Profile ID: " + updatedProfile.getSuperuserProfileId());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file.");
        }
    }
    
    @GetMapping("/getProfilePicture/{superuserId}")
public ResponseEntity<?> getSuperUserProfilePicture(@PathVariable int superuserId) {
    System.out.println("Fetching profile picture for SuperUser ID: " + superuserId); // Debug log

    Optional<SuperUserEntity> superUserOptional = superUserRepository.findById(superuserId); // Corrected method
    if (!superUserOptional.isPresent()) {
        return ResponseEntity.badRequest().body("Superuser not found.");
    }

    SuperUserProfileEntity superUserProfile = superUserProfileRepository.findBySuperuser(superUserOptional.get());
    if (superUserProfile != null && superUserProfile.getSuperuserProfilePicture() != null) {
        System.out.println("Profile picture found, returning byte array.");
        return ResponseEntity.ok(superUserProfile.getSuperuserProfilePicture());
    } else {
        System.out.println("No profile picture found, returning default image.");
        return ResponseEntity.ok(getDefaultProfilePicture()); // Ensure default picture path exists
    }
}

    private byte[] getDefaultProfilePicture() {
        try {
            // Ensure this path points to a valid image in your server
            Path path = Paths.get("public/default.png");
            System.out.println("Default profile picture path: " + path.toString()); // Log the path for debugging
            return Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

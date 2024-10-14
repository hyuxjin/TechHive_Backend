package com.example.admin_backend.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.admin_backend.Entity.ProfileEntity;
import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Repository.ProfileRepository;
import com.example.admin_backend.Repository.AdminRepository;
import com.example.admin_backend.Service.ProfileService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/admin/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AdminRepository arepo;

    @Autowired
    private ProfileRepository prepo;

    @PostMapping("/uploadProfilePicture")
    public ProfileEntity uploadProfilePicture(@RequestParam("adminId") int adminId, @RequestParam("file") MultipartFile file) {
        byte[] profilePicture = null;
        try {
            profilePicture = file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return profileService.saveProfilePicture(adminId, profilePicture);
    }

    @GetMapping("/getProfilePicture/{adminId}")
    public byte[] getProfilePicture(@PathVariable int adminId) {
        AdminEntity admin = arepo.findById(adminId).orElse(null);
        ProfileEntity profile = prepo.findByAdmin(admin);
        if (profile != null && profile.getProfilePicture() != null) {
            return profile.getProfilePicture();
        } else {
            try {
                Path path = Paths.get("public/default.png");
                return Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    // other controller methods
}

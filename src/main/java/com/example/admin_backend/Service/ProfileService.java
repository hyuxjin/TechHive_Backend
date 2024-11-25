package com.example.admin_backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.admin_backend.Entity.ProfileEntity;
import com.example.admin_backend.Entity.UserEntity;
import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Repository.ProfileRepository;
import com.example.admin_backend.Repository.UserRepository;
import com.example.admin_backend.Repository.AdminRepository;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminRepository adminRepository;

    // User Profile Methods
    public ProfileEntity saveUserProfilePicture(int userId, byte[] profilePicture) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        ProfileEntity profile = profileRepository.findByUser(user);
        if (profile == null) {
            profile = new ProfileEntity();
            profile.setUser(user);
        }
        profile.setProfilePicture(profilePicture);
        return profileRepository.save(profile);
    }

    public void deleteUserProfilePicture(int userId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        ProfileEntity profile = profileRepository.findByUser(user);
        if (profile != null) {
            profile.setProfilePicture(null);
            profileRepository.save(profile);
        }
    }

    public ProfileEntity getUserProfile(int userId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return profileRepository.findByUser(user);
    }

    // Admin Profile Methods
    public ProfileEntity saveAdminProfilePicture(int adminId, byte[] profilePicture) {
        AdminEntity admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
            
        ProfileEntity profile = profileRepository.findByAdmin(admin);
        if (profile == null) {
            profile = new ProfileEntity();
            profile.setAdmin(admin);
        }
        profile.setProfilePicture(profilePicture);
        return profileRepository.save(profile);
    }

    public void deleteAdminProfilePicture(int adminId) {
        AdminEntity admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
            
        ProfileEntity profile = profileRepository.findByAdmin(admin);
        if (profile != null) {
            profile.setProfilePicture(null);
            profileRepository.save(profile);
        }
    }

    public ProfileEntity getAdminProfile(int adminId) {
        AdminEntity admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        return profileRepository.findByAdmin(admin);
    }

    // Generic Profile Methods
    public byte[] getProfilePicture(int profileId) {
        ProfileEntity profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        return profile.getProfilePicture();
    }

    // Utility Methods
    public boolean isUserProfile(ProfileEntity profile) {
        return profile != null && profile.getUser() != null;
    }

    public boolean isAdminProfile(ProfileEntity profile) {
        return profile != null && profile.getAdmin() != null;
    }
}
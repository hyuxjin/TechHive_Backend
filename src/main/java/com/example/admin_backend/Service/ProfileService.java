package com.example.admin_backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.admin_backend.Entity.ProfileEntity;
import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Repository.ProfileRepository;
import com.example.admin_backend.Repository.AdminRepository;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AdminRepository adminRepository;

    public ProfileEntity saveProfilePicture(int adminId, byte[] profilePicture) {
        AdminEntity admin = adminRepository.findById(adminId).orElse(null);
        ProfileEntity profile = profileRepository.findByAdmin(admin);
        if (profile == null) {
            profile = new ProfileEntity();
            profile.setAdmin(admin);
        }
        profile.setProfilePicture(profilePicture);
        return profileRepository.save(profile);
    }
    
    // other service methods
}

package com.example.admin_backend.Service;

import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.admin_backend.Entity.SuperUserEntity;
import com.example.admin_backend.Entity.SuperUserProfileEntity;
import com.example.admin_backend.Repository.SuperUserProfileRepository;
import com.example.admin_backend.Repository.SuperUserRepository;

@Service
public class SuperUserProfileService {

    @Autowired
    private SuperUserProfileRepository superUserProfileRepository;

    @Autowired
    private SuperUserRepository superUserRepository;

    // Save or update profile picture for a superuser
    public SuperUserProfileEntity saveSuperUserProfilePicture(int superuserId, byte[] superuserProfilePicture) {
        // Retrieve the superuser by ID
        SuperUserEntity superuser = superUserRepository.findById(superuserId)
                .orElseThrow(() -> new NoSuchElementException("SuperUser with ID " + superuserId + " not found"));

        // Retrieve the superuser profile by superuser entity
        SuperUserProfileEntity superuserProfile = superUserProfileRepository.findBySuperuser(superuser);

        if (superuserProfile == null) {
            // If profile doesn't exist, create a new one
            superuserProfile = new SuperUserProfileEntity();
            superuserProfile.setSuperuser(superuser);
        }

        // Update profile picture
        superuserProfile.setSuperuserProfilePicture(superuserProfilePicture);

        // Save to repository
        return superUserProfileRepository.save(superuserProfile);
    }

    // Get profile by superuser entity
    public SuperUserProfileEntity getProfileBySuperUser(SuperUserEntity superuser) {
        return superUserProfileRepository.findBySuperuser(superuser);
    }
}

package com.example.admin_backend.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.admin_backend.Entity.SuperUserEntity;
import com.example.admin_backend.Repository.SuperUserRepository;

@Service
public class SuperUserService {

    @Autowired
    private SuperUserRepository superUserRepository;

    // Create or insert superuser record
    public SuperUserEntity insertSuperUser(SuperUserEntity superuser) {
        // Save the plain password (Not recommended for production, but following your request)
        superuser.setSuperUserPassword(superuser.getSuperUserPassword());

        System.out.println("Super Username: " + superuser.getSuperUsername()); // Debugging
        return superUserRepository.save(superuser);
    }

    // Read all records
    public List<SuperUserEntity> getAllSuperUsers() {
        return superUserRepository.findAll();
    }

    // Update a superuser password
    public SuperUserEntity updateSuperUser(int superuserId, String newSuperUserPassword, String currentSuperUserPassword) {
        SuperUserEntity superuser = superUserRepository.findById(superuserId)
                .orElseThrow(() -> new NoSuchElementException("SuperUser " + superuserId + " not found"));

        // Simple password comparison (plaintext comparison, no hashing)
        if (!superuser.getSuperUserPassword().equals(currentSuperUserPassword)) {
            throw new IllegalArgumentException("Current SuperUser password is incorrect.");
        }

        // Set the new password (No hashing)
        superuser.setSuperUserPassword(newSuperUserPassword);

        return superUserRepository.save(superuser);
    }

    // Get superuser by superusername
    public SuperUserEntity getSuperUserBySuperUsername(String superusername) {
        return superUserRepository.findBySuperusername(superusername)
                .orElseThrow(() -> new NoSuchElementException("SuperUser with username " + superusername + " not found"));
    }

    // Get superuser by ID number and password
    public SuperUserEntity getSuperUserBySuperUserIdNumberAndSuperUserPassword(String superuseridNumber, String superuserpassword) {
        Optional<SuperUserEntity> optionalSuperuser = superUserRepository.findBySuperuseridNumber(superuseridNumber);

        if (optionalSuperuser.isPresent() && optionalSuperuser.get().getSuperUserPassword().equals(superuserpassword)) {
            return optionalSuperuser.get();
        }
        return null; // Return null if the user is not found or password does not match
    }

    // Update the status of a superuser (enable/disable account)
    public SuperUserEntity updateSuperUserStatus(String superUserIdNumber, boolean newStatus) {
        SuperUserEntity superuser = superUserRepository.findBySuperuseridNumber(superUserIdNumber)
                .orElseThrow(() -> new NoSuchElementException("SuperUser with ID number " + superUserIdNumber + " not found"));

        superuser.setStatus(newStatus); // Update the status
        return superUserRepository.save(superuser);
    }
}

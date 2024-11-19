package com.example.admin_backend.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jakarta.annotation.PostConstruct;

import com.example.admin_backend.Entity.SuperUserEntity;
import com.example.admin_backend.Repository.SuperUserRepository;

@Service
public class SuperUserService {
   @Autowired
   private SuperUserRepository superUserRepository;

   private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

   @PostConstruct
   public void init() {
       hashExistingPasswords();
   }

   // Method to hash existing passwords
   public void hashExistingPasswords() {
       List<SuperUserEntity> superusers = superUserRepository.findAll();
       
       for(SuperUserEntity superuser : superusers) {
           if(!isPasswordHashed(superuser.getSuperUserPassword())) {
               superuser.setSuperUserPassword(encoder.encode(superuser.getSuperUserPassword()));
               superUserRepository.save(superuser);
           }
       }
   }

   // Helper method to check if password is already hashed 
   private boolean isPasswordHashed(String password) {
       return password.startsWith("$2a$");
   }

   public SuperUserEntity insertSuperUser(SuperUserEntity superuser) {
       superuser.setSuperUserPassword(encoder.encode(superuser.getSuperUserPassword()));
       return superUserRepository.save(superuser);
   }

   public SuperUserEntity updateSuperUser(int superuserId, String newPassword, String currentPassword) {
       SuperUserEntity superuser = superUserRepository.findById(superuserId)
               .orElseThrow(() -> new NoSuchElementException("SuperUser not found"));
               
       if (!encoder.matches(currentPassword, superuser.getSuperUserPassword())) {
           throw new IllegalArgumentException("Current password is incorrect");
       }
       
       superuser.setSuperUserPassword(encoder.encode(newPassword));
       return superUserRepository.save(superuser);
   }

   public SuperUserEntity getSuperUserBySuperUserIdNumberAndSuperUserPassword(String idNumber, String password) {
       Optional<SuperUserEntity> superuser = superUserRepository.findBySuperuseridNumber(idNumber);
       
       if (superuser.isPresent() && encoder.matches(password, superuser.get().getSuperUserPassword())) {
           return superuser.get();
       }
       return null;
   }

   // Other methods remain unchanged
   public List<SuperUserEntity> getAllSuperUsers() {
       return superUserRepository.findAll();
   }

   public SuperUserEntity getSuperUserBySuperUsername(String superusername) {
       return superUserRepository.findBySuperusername(superusername)
               .orElseThrow(() -> new NoSuchElementException("SuperUser with username " + superusername + " not found"));
   }

   public SuperUserEntity updateSuperUserStatus(String superUserIdNumber, boolean newStatus) {
       SuperUserEntity superuser = superUserRepository.findBySuperuseridNumber(superUserIdNumber)
               .orElseThrow(() -> new NoSuchElementException("SuperUser with ID number " + superUserIdNumber + " not found"));
       superuser.setStatus(newStatus);
       return superUserRepository.save(superuser);
   }
}

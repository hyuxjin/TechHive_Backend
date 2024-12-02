package com.example.admin_backend.Service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.admin_backend.Entity.UserEntity;
import com.example.admin_backend.Repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private EmailVerificationService emailVerificationService; // Delegate to this service

     @Transactional
    public UserEntity updateUserStatus(String idNumber, boolean status) {
        UserEntity user = userRepository.findByIdNumber(idNumber);
        if (user == null) {
            throw new NoSuchElementException("User not found with ID: " + idNumber);
        }
        user.setStatus(status);
        return userRepository.save(user);
    }
    
    @Transactional
    public UserEntity insertUser(UserEntity user) {
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPoints(50); // Set initial points to 50 upon signup
        UserEntity newUser = userRepository.save(user);

        // After saving the user, create a corresponding entry in the leaderboard
        leaderboardService.createInitialLeaderboardEntry(newUser.getUserId(), 50);

        return newUser;
    }

    public Iterable<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserEntity getUserByIdNumber(String idNumber) {
        return userRepository.findByIdNumber(idNumber);
    }

    public UserEntity getUserById(long id) {
        return userRepository.findById((int) id)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));
    }

    public UserEntity updateUserPassword(Integer userId, String newPassword, String currentPassword) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (passwordEncoder.matches(currentPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword)); // Hash new password
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Incorrect current password");
        }
    }

    public UserEntity authenticateUser(String idNumber, String password) {
        UserEntity user = userRepository.findByIdNumber(idNumber);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return user;
    }

    // Delegate verification code generation to EmailVerificationService
    public String generateVerificationCode() {
        return emailVerificationService.generateVerificationCode();
    }

   // Method to send the verification email
    public void sendVerificationCode(String email, String fullName) {
    emailVerificationService.sendVerificationCode(email, fullName);
}

    // Delegate email verification logic to EmailVerificationService
    public boolean verifyEmailCode(String email, String code) {
        return emailVerificationService.verifyEmailCode(email, code); // Corrected call
    }
}

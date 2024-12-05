package com.example.admin_backend.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper; // Corrected import
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.admin_backend.Entity.UserEntity;
import com.example.admin_backend.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class PasswordResetServiceUser {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetServiceUser(JavaMailSender mailSender, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generate a random reset code in the format CIT-XXXXXX.
     * @return Generated reset code.
     */
    public String generateResetCode() {
        int randomNum = (int) (Math.random() * 1000000); // Generates a 6-digit number
        return String.format("CIT-%06d", randomNum); // Formats to CIT-XXXXXX
    }

    /**
     * Send the reset code via email and store it in the database.
     * @param email The user's email address.
     * @param resetCode The generated reset code.
     */
    public void sendResetCode(String email, String resetCode) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));

        // Store the reset code and expiration in the UserEntity
        user.setResetCode(resetCode);
        user.setResetCodeExpiration(LocalDateTime.now().plusMinutes(30)); // 30 minutes expiration
        userRepository.save(user); // Save the user with the new reset code and expiration

        // Create the email content
        String subject = "Your Password Reset Code";
        String body = "<p>Hi " + user.getFullName() + ",</p>" +
                      "<p>We received a request to reset your password for your Wildcat One Tap account at Cebu Institute of Technology - University. " +
                      "Please use the following code to reset your password:</p>" +
                      "<p><b>Your Reset Code: " + resetCode + "</b></p>" +
                      "<p>Note: This reset code will expire in 30 minutes.</p>" +
                      "<p>If you did not request a password reset, please ignore this email.</p>" +
                      "<p>Thank you,<br>The Wildcat One Tap Team</p>";

        // Send the email
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true); // Set to true for HTML content
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    /**
     * Verify if the reset code matches the stored code for the email.
     * @param email The user's email address.
     * @param resetCode The reset code to verify.
     * @return true if the reset code is valid and not expired, false otherwise.
     */
    public boolean verifyResetCode(String email, String resetCode) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));

        String storedCode = user.getResetCode(); // Get the reset code from the database
        LocalDateTime expirationTime = user.getResetCodeExpiration(); // Get the expiration time

        // Check if the reset code matches and hasn't expired
        return storedCode != null && storedCode.equals(resetCode) &&
               expirationTime != null && LocalDateTime.now().isBefore(expirationTime);
    }

    /**
     * Reset the user's password.
     * @param email The user's email address.
     * @param newPassword The new password to set.
     */
    public void resetPassword(String email, String newPassword) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));

        // Encode and update the new password
        user.setPassword(passwordEncoder.encode(newPassword));

        // Clear the reset code after a successful password reset
        user.setResetCode(null);
        user.setResetCodeExpiration(null);

        userRepository.save(user);
    }
}

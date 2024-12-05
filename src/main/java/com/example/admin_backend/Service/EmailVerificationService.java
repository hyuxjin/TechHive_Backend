package com.example.admin_backend.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailVerificationService {

    @Autowired
    private JavaMailSender mailSender;

    private final Map<String, VerificationData> verificationMap = new HashMap<>();

    /**
     * Generate a random 6-digit verification code prefixed with "CIT-".
     *
     * @return Generated verification code.
     */
    public String generateVerificationCode() {
        int randomNum = (int) (Math.random() * 1000000); // Generates a 6-digit number
        return String.format("CIT-%06d", randomNum);
    }

    /**
     * Send a verification code to the provided email address and store it in the verification map.
     *
     * @param email The recipient's email address.
     * @param fullName The full name of the user for the personalized email content.
     */
    public void sendVerificationCode(String email, String fullName) {
      String code = generateVerificationCode();
      verificationMap.put(email, new VerificationData(code, LocalDateTime.now().plusMinutes(30)));
  
      // Default to "User" if fullName is null or empty
      if (fullName == null || fullName.trim().isEmpty()) {
          fullName = "User";
      }
  
      String subject = "Email Verification Code - Wildcat One Tap";
      String body = "<p>Hi " + fullName + ",</p>" +
                    "<p>Thank you for signing up for a Wildcat One Tap account at Cebu Institute of Technology - University.</p>" +
                    "<p>Before you can successfully complete your registration, please use the following code to verify your email address:</p>" +
                    "<p><b>Your Verification Code: " + code + "</b></p>" +
                    "<p>Note: This verification code will expire in 30 minutes.</p>" +
                    "<p>If you did not sign up for a Wildcat One Tap account, please ignore this email.</p>" +
                    "<p>Thank you,<br>The Wildcat One Tap Team</p>";
  
      try {
          MimeMessage message = mailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(message, true);
          helper.setTo(email);
          helper.setSubject(subject);
          helper.setText(body, true); // Enable HTML
          mailSender.send(message);
      } catch (MessagingException e) {
          throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
      }
  }
  

    /**
     * Verify the provided code for the specified email address.
     *
     * @param email The recipient's email address.
     * @param code  The verification code to validate.
     * @return True if the code is valid and not expired, false otherwise.
     */
    public boolean verifyEmailCode(String email, String code) {
        VerificationData data = verificationMap.get(email);

        if (data == null) {
            return false; // No verification data
        }

        if (!data.getCode().equals(code)) {
            return false; // Mismatched code
        }

        if (data.isExpired()) {
            verificationMap.remove(email); // Remove expired data
            return false; // Expired code
        }

        verificationMap.remove(email); // Clear data on successful verification
        return true; // Code is valid
    }

    /**
     * Inner class to hold verification data.
     */
    private static class VerificationData {
        private final String code;
        private final LocalDateTime expiration;

        public VerificationData(String code, LocalDateTime expiration) {
            this.code = code;
            this.expiration = expiration;
        }

        public String getCode() {
            return code;
        }

        /**
         * Check if the verification code has expired.
         *
         * @return True if the code has expired, false otherwise.
         */
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiration);
        }
    }
}

package com.example.admin_backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailTestController {

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/test-email")
    public String sendTestEmail(@RequestParam String recipient) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient); // Email address you want to test
            message.setSubject("Test Email");
            message.setText("This is a test email sent from Spring Boot using Gmail SMTP.");
            mailSender.send(message);
            return "Email sent successfully to " + recipient;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error sending email: " + e.getMessage();
        }
    }
}

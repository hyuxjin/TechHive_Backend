package com.example.admin_backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.admin_backend.Entity.ContactMessageEntity;
import com.example.admin_backend.Service.ContactMessageService;

@RestController
@RequestMapping("/contact")
@CrossOrigin(origins = "http://localhost:3000")
public class ContactMessageController {
    @Autowired
    private ContactMessageService contactMessageService;

    @PostMapping
    public ResponseEntity<?> submitContactForm(@RequestBody ContactMessageEntity message) {
        try {
            contactMessageService.saveAndSendMessage(message);
            return ResponseEntity.ok().body("Message sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send message");
        }
    }
}

package com.example.admin_backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.example.admin_backend.Repository.ContactMessageRepository;
import com.example.admin_backend.Entity.ContactMessageEntity;

@Service
public class ContactMessageService {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void saveAndSendMessage(ContactMessageEntity message) {
        contactMessageRepository.save(message);
        sendEmail(message);
    }

    private void sendEmail(ContactMessageEntity message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo("wildcatonetap@gmail.com");
        mailMessage.setSubject("New Contact Us Submission from " + message.getFullName()); // Use getFullName()

        String emailBody = "Dear Wildcat One Tap Team,\n\n" +
                           "We have received a new contact form submission from a visitor. Below are the details:\n\n" +
                           "Name: " + message.getFullName() + "\n" +  // Use getFullName()
                           "Email: " + message.getEmail() + "\n" +
                           "Phone: " + message.getPhoneNumber() + "\n" +
                           "Message:\n\n" +
                           message.getMessage() + "\n\n" +
                           "Please follow up with the visitor at your earliest convenience.\n\n" +
                           "Thank you!\n" +
                           "Wildcat One Tap Team";

        mailMessage.setText(emailBody);
        mailMessage.setReplyTo(message.getEmail());  // Set the Reply-To header
        mailSender.send(mailMessage);
    }
}

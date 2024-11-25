package com.example.admin_backend.Controller;

import com.example.admin_backend.Entity.EntryEntity;
import com.example.admin_backend.Entity.UserEntity;
import com.example.admin_backend.Service.EntryService;
import com.example.admin_backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/entries")
@CrossOrigin(origins = "http://localhost:3000")
public class EntryController {

    @Autowired
    private EntryService entryService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<EntryEntity> createEntry(
        @RequestPart("user") String userId, 
        @RequestPart("idNumber") String idNumber, 
        @RequestPart("fullName") String fullName, 
        @RequestPart("level") String level, 
        @RequestPart("type") String type, 
        @RequestPart("photo") MultipartFile photo) throws IOException {
        
        System.out.println("Received userId: " + userId);
        System.out.println("Received idNumber: " + idNumber);
        System.out.println("Received fullName: " + fullName);
        System.out.println("Received level: " + level);
        System.out.println("Received type: " + type);
        System.out.println("Received photo: " + (photo != null ? "photo present" : "photo null"));

        EntryEntity entryEntity = new EntryEntity();
        // Fetch the UserEntity based on the userId
        UserEntity user = userService.getUserById(Long.parseLong(userId));
        entryEntity.setUser(user);
        entryEntity.setIdNumber(idNumber);
        entryEntity.setFullName(fullName);
        entryEntity.setLevel(level);
        entryEntity.setType(type);
        if (photo != null) {
            entryEntity.setPhoto(photo.getBytes());
        }

        EntryEntity savedEntry = entryService.saveEntry(entryEntity);
        return new ResponseEntity<>(savedEntry, HttpStatus.CREATED);
    }

    @GetMapping("/{entryId}")
    public ResponseEntity<EntryEntity> getEntryById(@PathVariable Long entryId) {
        Optional<EntryEntity> entryEntity = entryService.getEntryById(entryId);
        return entryEntity.map(ResponseEntity::ok)
                          .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<EntryEntity>> getAllEntries() {
        List<EntryEntity> entries = entryService.getAllEntries();
        return new ResponseEntity<>(entries, HttpStatus.OK);
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long entryId) {
        entryService.deleteEntry(entryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        return new ResponseEntity<>("Error processing the image: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
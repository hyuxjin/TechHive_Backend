package com.example.admin_backend.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.admin_backend.Entity.SuperUserEntity;
import com.example.admin_backend.Service.SuperUserService;

@RestController
@RequestMapping("/superuser")
@CrossOrigin(origins = "http://localhost:3000")
public class SuperUserController {

    @Autowired
    private SuperUserService superUserService; // Correct naming of the service

    @GetMapping("/print")
    public String itWorks() {
        return "It works";
    }

    // Create
    @PostMapping("/insertSuperUser")
    public ResponseEntity<SuperUserEntity> insertSuperUser(@RequestBody SuperUserEntity superUser) {
        // Ensure superUser has a non-null superusername
        if (superUser.getSuperUsername() == null || superUser.getSuperUsername().isEmpty()) { 
            return ResponseEntity.badRequest().body(null); // Error if superUsername is null
        }
        try {
            SuperUserEntity createdUser = superUserService.insertSuperUser(superUser);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // Corrected from Response.Entity
        }
    }

    // Read
    @GetMapping("/getAllSuperUsers")
    public List<SuperUserEntity> getAllSuperUsers() {
        return superUserService.getAllSuperUsers();
    }

    // Update a superuser record
    @PutMapping("/updateSuperUserPassword")
public ResponseEntity<?> updateSuperUserPassword(@RequestParam Integer superuserId, 
                                                 @RequestBody Map<String, String> requestBody) {
    String currentSuperUserPassword = requestBody.get("currentSuperUserPassword");
    String newSuperUserPassword = requestBody.get("newSuperUserPassword");


        try {
            SuperUserEntity updatedSuperUser = superUserService.updateSuperUser(superuserId, newSuperUserPassword, currentSuperUserPassword);
            return ResponseEntity.ok(updatedSuperUser);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    // Sign-in
    @PostMapping("/signin")
public ResponseEntity<?> signIn(@RequestBody Map<String, String> loginData) {
    String superUserIdNumber = loginData.get("superUserIdNumber");  // Corrected variable name
    String superUserPassword = loginData.get("superUserPassword");

    try {
        SuperUserEntity superuser = superUserService.getSuperUserBySuperUserIdNumberAndSuperUserPassword(superUserIdNumber, superUserPassword);
        if (superuser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Super User ID Number or Super User password.");
        }

        // Check if the account is disabled
        if (!superuser.getStatus()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is disabled.");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("token", "dummyToken");  // Replace with actual token if using JWT
        response.put("superuserId", superuser.getSuperUserId());
        response.put("superUsername", superuser.getSuperUsername());
        response.put("fullName", superuser.getFullName());
        response.put("email", superuser.getEmail());
        response.put("superUserIdNumber", superuser.getSuperUserIdNumber());
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during sign-in.");
    }
}


    // Get superuser by ID number
    @GetMapping("/getBySuperUsername")
    public ResponseEntity<SuperUserEntity> getSuperUserBySuperUsername(@RequestParam String superUsername) {
        SuperUserEntity superuser = superUserService.getSuperUserBySuperUsername(superUsername); // Corrected method call
        if (superuser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(superuser);
    }

    @PutMapping("/updateStatus")
public ResponseEntity<?> updateSuperUserStatus(@RequestBody Map<String, Object> requestBody) {
    String superUserIdNumber = (String) requestBody.get("superUserIdNumber");
    boolean newStatus = (Boolean) requestBody.get("status");

    try {
        SuperUserEntity updatedSuperUser = superUserService.updateSuperUserStatus(superUserIdNumber, newStatus);
        return ResponseEntity.ok(updatedSuperUser);
    } catch (NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating status.");
    }
}

}

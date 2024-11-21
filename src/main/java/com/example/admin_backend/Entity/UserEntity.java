package com.example.admin_backend.Entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tbluser")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "idnumber")
    private String idNumber;

    @Column(name = "role")
    private String role;  // Role: either 'USER' or 'ADMIN'

    @Column(name = "points", nullable = false)
    private int points = 50; // Default points for new users

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LocationEntity> locations;  // List of locations associated with the user

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ReportEntity> reports;  // List of reports submitted by the user

    // Added fields for password reset
    @Column(name = "reset_code")
    private String resetCode;

    @Column(name = "reset_code_expiration")
    private LocalDateTime resetCodeExpiration;

    @Column(name = "status", nullable = false)
    private boolean status = true;

    public UserEntity() {}

    public UserEntity(String username, String email, String password, String fullName, String idNumber, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.idNumber = idNumber;
        this.role = role;
        this.points = 50; // Initialize with default points
    }

    // ************ Point System Methods ************ //
    public void addPoints(int points) {
        this.points += points;
    }

    public void subtractPoints(int points) {
        this.points = Math.max(this.points - points, 0); // Ensure points don't go below 0
    }

    public void addLikePoints() {
        this.points += 1;
    }

    public void addDislikePoints() {
        this.points -= 1;
    }

    public void removeLikePoints() {
        if (this.points > 0) {
            this.points -= 1;
        }
    }

    public void removeDislikePoints() {
        this.points += 1;
    }

    public void resetPoints() {
        this.points = Math.max(this.points, 0);
    }

    // ************ RBAC Methods ************ //
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    public boolean isUser() {
        return "USER".equalsIgnoreCase(this.role);
    }

    // ************ Getters and Setters for password reset ************ //
    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public LocalDateTime getResetCodeExpiration() {
        return resetCodeExpiration;
    }

    public void setResetCodeExpiration(LocalDateTime resetCodeExpiration) {
        this.resetCodeExpiration = resetCodeExpiration;
    }

    // ************ Getters and Setters ************ //
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<LocationEntity> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationEntity> locations) {
        this.locations = locations;
    }

    public List<ReportEntity> getReports() {
        return reports;
    }

    public void setReports(List<ReportEntity> reports) {
        this.reports = reports;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    
}


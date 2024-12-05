package com.example.admin_backend.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblsuperuser")
public class SuperUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "superuserid", nullable = false, updatable = false)
    private int superUserId;

    @Column(name = "superusername", nullable = false, unique = true)
    private String superUsername;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "superuserpassword", nullable = false)
    private String superUserPassword;

    @Column(name = "fullname", nullable = false)
    private String fullName;

    @Column(name = "superuseridnumber", nullable = false, unique = true)
    private String superUserIdNumber;

    @Column(name = "status", nullable = false)
    private boolean status;

    @Column(name = "reset_code")
    private String resetCode;

    @Column(name = "reset_code_timestamp")
    private LocalDateTime resetCodeTimestamp;

    @Column(name = "reset_code_verified")
    private Boolean resetCodeVerified = false; // Default value is false

    // Getters and Setters
    public int getSuperUserId() {
        return superUserId;
    }

    public void setSuperUserId(int superUserId) {
        this.superUserId = superUserId;
    }

    public String getSuperUsername() {
        return superUsername;
    }

    public void setSuperUsername(String superUsername) {
        this.superUsername = superUsername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSuperUserPassword() {
        return superUserPassword;
    }

    public void setSuperUserPassword(String superUserPassword) {
        this.superUserPassword = superUserPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSuperUserIdNumber() {
        return superUserIdNumber;
    }

    public void setSuperUserIdNumber(String superUserIdNumber) {
        this.superUserIdNumber = superUserIdNumber;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public LocalDateTime getResetCodeTimestamp() {
        return resetCodeTimestamp;
    }

    public void setResetCodeTimestamp(LocalDateTime resetCodeTimestamp) {
        this.resetCodeTimestamp = resetCodeTimestamp;
    }

    public Boolean getResetCodeVerified() {
        return resetCodeVerified;
    }

    public void setResetCodeVerified(Boolean resetCodeVerified) {
        this.resetCodeVerified = resetCodeVerified;
    }
}

package com.example.admin_backend.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbladmin")
public class AdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adminId", nullable = false, updatable = false)
    private int adminId;

    @Column(name = "adminname", nullable = false, unique = true)
    private String adminname;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "fullname", nullable = false)
    private String fullName;

    @Column(name = "idnumber", nullable = false, unique = true)
    private String idNumber;

    @Column(name = "status", nullable = false)
    private boolean status;

    @Column(name = "reset_code")
    private String resetCode;

    @Column(name = "reset_code_timestamp")
    private LocalDateTime resetCodeTimestamp;

    @Column(name = "reset_code_verified")
    private Boolean resetCodeVerified = false; // Default to false

    // Constructors
    public AdminEntity() {
        super();
    }

    public AdminEntity(int adminId, String adminname, String email, String password, String fullName, String idNumber, boolean status) {
        super();
        this.adminId = adminId;
        this.adminname = adminname;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.idNumber = idNumber;
        this.status = status;
    }

    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getAdminname() {
        return adminname;
    }

    public void setAdminname(String adminname) {
        this.adminname = adminname;
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

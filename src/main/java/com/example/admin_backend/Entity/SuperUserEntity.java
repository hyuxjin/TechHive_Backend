package com.example.admin_backend.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblsuperuser")
public class SuperUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "superuserid")  // Map to the database primary key column name
    private int superuserId;

    @Column(name = "superusername", nullable = false)
    private String superusername;

    @Column(name = "email")  // Match the field exactly
    private String email;

    @Column(name = "superuserpassword")  // Match the field exactly
    private String superuserpassword;

    @Column(name = "fullname")  // Match the field exactly
    private String fullName;

    @Column(name = "superuseridnumber")  // Match the field exactly
    private String superuseridNumber;

    @Column(name = "status")
    private boolean status;

    // Constructor, getters, and setters
    public SuperUserEntity() {
        super();
    }

    public SuperUserEntity(int superuserId, String superusername, String email, String superuserpassword, String fullName, String superuseridNumber) {
        this.superuserId = superuserId;
        this.superusername = superusername;
        this.email = email;
        this.superuserpassword = superuserpassword;
        this.fullName = fullName;
        this.superuseridNumber = superuseridNumber;
        this.status = status;
    }

    // Getters and Setters

    public int getSuperUserId() {
        return superuserId;
    }

    public void setSuperUserId(int superuserId) {
        this.superuserId = superuserId;
    }

    public String getSuperUsername() {
        return superusername;
    }

    public void setSuperUsername(String superusername) {
        this.superusername = superusername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSuperUserPassword() {
        return superuserpassword;
    }

    public void setSuperUserPassword(String superuserpassword) {
        this.superuserpassword = superuserpassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSuperUserIdNumber() {
        return superuseridNumber;
    }

    public void setSuperUserIdNumber(String superuseridNumber) {
        this.superuseridNumber = superuseridNumber;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

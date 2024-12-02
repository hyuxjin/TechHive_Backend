package com.example.admin_backend.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblprofile")
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private int profile_id;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "adminId", referencedColumnName = "adminId")
    private AdminEntity admin;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] profilePicture;

    // Default constructor
    public ProfileEntity() {
    }

    // Constructor for User profile
    public ProfileEntity(int profile_id, UserEntity user, byte[] profilePicture) {
        this.profile_id = profile_id;
        this.user = user;
        this.profilePicture = profilePicture;
    }

    // Constructor for Admin profile
    public ProfileEntity(int profile_id, AdminEntity admin, byte[] profilePicture) {
        this.profile_id = profile_id;
        this.admin = admin;
        this.profilePicture = profilePicture;
    }

    // Getters and Setters
    public int getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(int profile_id) {
        this.profile_id = profile_id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public AdminEntity getAdmin() {
        return admin;
    }

    public void setAdmin(AdminEntity admin) {
        this.admin = admin;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    // Utility methods to check profile type
    public boolean isUserProfile() {
        return user != null;
    }

    public boolean isAdminProfile() {
        return admin != null;
    }
}
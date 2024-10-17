package com.example.admin_backend.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "reports")
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;  // Related user entity

    @Column(nullable = false)
    private String idNumber;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String level;

    @Column(nullable = false)
    private String type;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photo;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_approved")
    private Boolean approved;

    @Column(name = "is_declined")
    private Boolean declined;

    // Default constructor
    public ReportEntity() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructor with fields
    public ReportEntity(UserEntity user, String idNumber, String fullName, String level, String type, byte[] photo, Boolean approved, Boolean declined) {
        this.user = user;
        this.idNumber = idNumber;
        this.fullName = fullName;
        this.level = level;
        this.type = type;
        this.photo = photo;
        this.createdAt = LocalDateTime.now();
        this.approved = approved;
        this.declined = declined;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean getDeclined() {
        return declined;
    }

    public void setDeclined(Boolean declined) {
        this.declined = declined;
    }

    @Override
    public String toString() {
        return "ReportEntity{" +
                "id=" + id +
                ", user=" + user +
                ", idNumber='" + idNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", level='" + level + '\'' +
                ", type='" + type + '\'' +
                ", createdAt=" + createdAt +
                ", approved=" + approved +
                ", declined=" + declined +
                '}';
    }
}
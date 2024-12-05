package com.example.admin_backend.Entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "entries")
public class EntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

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

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    // Default constructor
    public EntryEntity() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructor with fields
    public EntryEntity(UserEntity user, String idNumber, String fullName, String level, String type, byte[] photo) {
        this.user = user;
        this.idNumber = idNumber;
        this.fullName = fullName;
        this.level = level;
        this.type = type;
        this.photo = photo;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
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

    // toString method for debugging
    @Override
    public String toString() {
        return "EntryEntity{" +
                "entryId=" + entryId +
                ", user=" + user +
                ", idNumber='" + idNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", level='" + level + '\'' +
                ", type='" + type + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
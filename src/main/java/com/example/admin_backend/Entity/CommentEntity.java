package com.example.admin_backend.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblcomment")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "post_id", nullable = false)
    private int postId;

    @Column(name = "admin_id", nullable = false)
    private int adminId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    // Default constructor
    public CommentEntity() {}

    // Constructor without commentId
    public CommentEntity(String content, int postId, int adminId, String fullName, String idNumber) {
        this.content = content;
        this.postId = postId;
        this.adminId = adminId;
        this.fullName = fullName;
        this.idNumber = idNumber;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters

    public Integer getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public String toString() {
        return "CommentEntity{" +
                "commentId=" + commentId +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", postId=" + postId +
                ", adminId=" + adminId +
                ", fullName='" + fullName + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}

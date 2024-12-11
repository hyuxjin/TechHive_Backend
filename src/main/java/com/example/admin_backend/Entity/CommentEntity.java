package com.example.admin_backend.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "tblcomment")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @Column(name = "content", nullable = false)
    private String content;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "Asia/Manila")
@Column(name = "timestamp")
private LocalDateTime timestamp;

    @Column(name = "post_id", nullable = false)
    private int postId;

    @Column(name = "user_id", nullable = true)
    private Integer userId;

    @Column(name = "admin_id", nullable = true)
    private Integer adminId;

    @Column(name = "superuser_id", nullable = true)
    private Integer superUserId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Column(name = "visible")
    private boolean visible = true; // Default visibility is true

    // Default constructor
    public CommentEntity() {}

    // Constructor for users
    public CommentEntity(String content, int postId, int userId, String fullName, String idNumber) {
        this.content = content;
        this.postId = postId;
        this.userId = userId;
        this.fullName = fullName;
        this.idNumber = idNumber;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for admins/superusers
    public CommentEntity(String content, int postId, Integer adminId, Integer superUserId, String fullName, String idNumber, boolean visible) {
        this.content = content;
        this.postId = postId;
        this.adminId = adminId;
        this.superUserId = superUserId;
        this.fullName = fullName;
        this.idNumber = idNumber;
        this.timestamp = LocalDateTime.now();
        this.visible = visible;
    }

    // Getters and setters

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getSuperUserId() {
        return superUserId;
    }

    public void setSuperUserId(Integer superUserId) {
        this.superUserId = superUserId;
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "CommentEntity{" +
                "commentId=" + commentId +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", postId=" + postId +
                ", userId=" + userId +
                ", adminId=" + adminId +
                ", superUserId=" + superUserId +
                ", fullName='" + fullName + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", isDeleted=" + isDeleted +
                ", visible=" + visible +
                '}';
    }
}

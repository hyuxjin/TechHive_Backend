package com.example.admin_backend.Entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblpost")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postId;

    @Column(name = "content")
    private String content;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "admin_id", nullable = true)  // Nullable since not all posts are from admins
    private Integer adminId;

    @Column(name = "superuser_id", nullable = true)  // Nullable since not all posts are from superusers
    private Integer superUserId;

    @Column(name = "is_visible")
    private boolean isVisible;

    @Column(name = "likes")
    private int likes;

    @Column(name = "dislikes")
    private int dislikes;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "idnumber")
    private String idNumber;

    @Column(name = "image", columnDefinition = "LONGTEXT")
    private String image;

    @ManyToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "profile_id") // Associate the profile
    private ProfileEntity profile;  // Reference to ProfileEntity

    // Use ElementCollection to store the IDs of the users who liked or disliked the post
    @ElementCollection
    @CollectionTable(name = "post_likes", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "user_id")
    private Set<Integer> likedBy = new HashSet<>();  // Stores the IDs of admins/superusers who liked the post

    @ElementCollection
    @CollectionTable(name = "post_dislikes", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "user_id")
    private Set<Integer> dislikedBy = new HashSet<>();  // Stores the IDs of admins/superusers who disliked the post

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public PostEntity() {
        // Default constructor
    }

    public PostEntity(int postId, String content, LocalDateTime timestamp, Integer adminId, Integer superUserId, boolean isVisible, int likes,
                      int dislikes, String fullName, String idNumber, String image, ProfileEntity profile) {
        this.postId = postId;
        this.content = content;
        this.timestamp = timestamp;
        this.adminId = adminId;
        this.superUserId = superUserId;
        this.isVisible = isVisible;
        this.likes = likes;
        this.dislikes = dislikes;
        this.fullName = fullName;
        this.idNumber = idNumber;
        this.image = image;
        this.profile = profile;
    }

    // Getters and Setters

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
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

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }

    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    public Set<Integer> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(Set<Integer> likedBy) {
        this.likedBy = likedBy;
    }

    public Set<Integer> getDislikedBy() {
        return dislikedBy;
    }

    public void setDislikedBy(Set<Integer> dislikedBy) {
        this.dislikedBy = dislikedBy;
    }
}

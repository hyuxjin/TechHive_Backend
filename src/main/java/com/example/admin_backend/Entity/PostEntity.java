package com.example.admin_backend.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tblpost")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;
    
    private String content;
    private LocalDateTime timestamp;
    private Integer userId;
    private Integer adminId;
    private Integer superUserId;
    private String userRole;
    
    @Column(name = "isverified")
    private boolean isVerified;
    
    @Column(name = "is_visible")
    private boolean visible = true;
    
    private int likes;
    private int dislikes;
    private String fullName;
    private String idNumber;
    
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;
    
    @Column(columnDefinition = "LONGTEXT")
    private String image;
    
    @ElementCollection
    @CollectionTable(name = "post_likes", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "user_id")
    private Set<Integer> likedBy = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "post_dislikes", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "user_id")
    private Set<Integer> dislikedBy = new HashSet<>();
    
    @Column(name = "is_deleted")
    private boolean isDeleted = false;
    
    private Boolean isSubmittedReport = false;
    private String status;
    private String adminNotes;
    private Integer lastModifiedBy;
    private LocalDateTime lastModifiedAt;

    // Getters and Setters
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }

    public Integer getSuperUserId() { return superUserId; }
    public void setSuperUserId(Integer superUserId) { this.superUserId = superUserId; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public int getDislikes() { return dislikes; }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public ProfileEntity getProfile() { return profile; }
    public void setProfile(ProfileEntity profile) { this.profile = profile; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Set<Integer> getLikedBy() { return likedBy; }
    public void setLikedBy(Set<Integer> likedBy) { this.likedBy = likedBy; }

    public Set<Integer> getDislikedBy() { return dislikedBy; }
    public void setDislikedBy(Set<Integer> dislikedBy) { this.dislikedBy = dislikedBy; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public Boolean getIsSubmittedReport() { return isSubmittedReport; }
    public void setIsSubmittedReport(Boolean submittedReport) { isSubmittedReport = submittedReport; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public Integer getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(Integer lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public LocalDateTime getLastModifiedAt() { return lastModifiedAt; }
    public void setLastModifiedAt(LocalDateTime lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; }

    @Override
    public String toString() {
        return "PostEntity{" +
                "postId=" + postId +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", userId=" + userId +
                ", isVerified=" + isVerified +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", fullName='" + fullName + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", profile='" + profile + '\'' +
                ", image='" + (image != null ? "image present" : "no image") + '\'' +
                ", likedBy=" + likedBy +
                ", dislikedBy=" + dislikedBy +
                ", isDeleted=" + isDeleted +
                ", isSubmittedReport=" + isSubmittedReport + 
                ", status='" + status + '\'' +
                '}';
    }
}
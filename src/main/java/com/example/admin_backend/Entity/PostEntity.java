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
<<<<<<< Updated upstream
    private boolean visible = true;
    
    private int likes;
    private int dislikes;
    private String fullName;
    private String idNumber;
=======
    private Boolean isVisible = true;
    
    private Integer likes;
    private Integer dislikes;
    
    @Column(name = "fullname")
    private String fullname;
    
    @Column(name = "idnumber")
    private String idnumber;
>>>>>>> Stashed changes
    
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
    private Boolean isDeleted = false;
    
<<<<<<< Updated upstream
    private Boolean isSubmittedReport = false;
=======
    @Column(name = "is_submitted_report")
    private Boolean isSubmittedReport;
    
    @Column(name = "status")
>>>>>>> Stashed changes
    private String status;
    private String adminNotes;
<<<<<<< Updated upstream
=======

    @Column(name = "reportid")
    private Integer reportId;
    
    @Column(name = "last_modified_by")
>>>>>>> Stashed changes
    private Integer lastModifiedBy;
    private LocalDateTime lastModifiedAt;

<<<<<<< Updated upstream
    // Getters and Setters
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
=======
    // Standard getters and setters
    public int getPostId() {
        return postId;
    }
>>>>>>> Stashed changes

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

    public Integer getUserId() {
        return userId;
    }

<<<<<<< Updated upstream
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
=======
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAdminId() {
        return adminId;
    }
>>>>>>> Stashed changes

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getSuperUserId() {
        return superUserId;
    }

    public void setSuperUserId(Integer superUserId) {
        this.superUserId = superUserId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    // Methods for boolean fields with both styles
    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean verified) {
        this.isVerified = verified;
    }

<<<<<<< Updated upstream
    public Boolean getIsSubmittedReport() { return isSubmittedReport; }
    public void setIsSubmittedReport(Boolean submittedReport) { isSubmittedReport = submittedReport; }
=======
    public boolean isVisible() {
        return isVisible;
    }
>>>>>>> Stashed changes

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean visible) {
        this.isVisible = visible;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    // Methods for name fields with both styles
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getFullName() {
        return fullname;
    }

    public void setFullName(String fullName) {
        this.fullname = fullName;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public String getIdNumber() {
        return idnumber;
    }

    public void setIdNumber(String idNumber) {
        this.idnumber = idNumber;
    }

    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        this.isDeleted = deleted;
    }

    public Boolean getIsSubmittedReport() {
        return isSubmittedReport;
    }

    public void setIsSubmittedReport(Boolean isSubmittedReport) {
        this.isSubmittedReport = isSubmittedReport;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public Integer getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Integer lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    @Override
    public String toString() {
        return "PostEntity{" +
                "postId=" + postId +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", userId=" + userId +
                ", adminId=" + adminId +
                ", superUserId=" + superUserId +
                ", userRole='" + userRole + '\'' +
                ", isVerified=" + isVerified +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", fullname='" + fullname + '\'' +
                ", idnumber='" + idnumber + '\'' +
                ", image='" + (image != null ? "image present" : "no image") + '\'' +
                ", isDeleted=" + isDeleted +
                ", isSubmittedReport=" + isSubmittedReport + 
                ", status='" + status + '\'' +
                ", reportId=" + reportId +
                ", adminNotes='" + adminNotes + '\'' +
                ", lastModifiedBy=" + lastModifiedBy +
                ", lastModifiedAt=" + lastModifiedAt +
                '}';
    }
}
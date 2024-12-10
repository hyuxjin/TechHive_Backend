package com.example.admin_backend.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tblpost")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postId;
    
    @Column(columnDefinition = "varchar(500)")
    private String content;
    
    private LocalDateTime timestamp;
    
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "admin_id")
    private Integer adminId;
    
    @Column(name = "superuser_id")
    private Integer superUserId;
    
    @Column(name = "user_role")
    private String userRole;
    
    @Column(name = "isverified")
    private Boolean isVerified = false;
    
    @Column(name = "is_visible")
    private Boolean isVisible = true;
    
    @Column(name = "likes")
    private Integer likes = 0;
    
    @Column(name = "dislikes")
    private Integer dislikes = 0;
    
    @ElementCollection
@CollectionTable(name = "post_liked_by", 
    joinColumns = @JoinColumn(name = "post_id"))
@Column(name = "user_identifier")  // Will store "userId_role"
private Set<String> likedBy = new HashSet<>();

@ElementCollection
@CollectionTable(name = "post_disliked_by", 
    joinColumns = @JoinColumn(name = "post_id"))
@Column(name = "user_identifier")  // Will store "userId_role"
private Set<String> dislikedBy = new HashSet<>();
    
    @Column(name = "fullname")
    private String fullname;
    
    @Column(name = "idnumber")
    private String idnumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ProfileEntity profile;
    
    @Column(columnDefinition = "LONGTEXT")
    private String image;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @Column(name = "is_submitted_report")
    private Boolean isSubmittedReport;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "admin_notes")
    private String adminNotes;

    @Column(name = "reportid")
    private Integer reportId;
    
    @Column(name = "last_modified_by")
    private Integer lastModifiedBy;
    
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    // Constructor
    public PostEntity() {}

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

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean verified) {
        this.isVerified = verified;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean visible) {
        this.isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
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

    public Set<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(Set<String> likedBy) {
        this.likedBy = likedBy;
    }

    public Set<String> getDislikedBy() {
        return dislikedBy;
    }

    public void setDislikedBy(Set<String> dislikedBy) {
        this.dislikedBy = dislikedBy;
    }

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

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        this.isDeleted = deleted;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
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

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
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
                ", isVisible=" + isVisible +
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
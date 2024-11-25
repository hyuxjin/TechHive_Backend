package com.example.admin_backend.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblreport")
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportId;

    @Column(name = "description")
    private String description;

    @Column(name = "location")
    private String location;

    @Column(name = "image1_path")
    private String image1Path;

    @Column(name = "image2_path")
    private String image2Path;

    @Column(name = "image3_path")
    private String image3Path;

    @Column(name = "report_type")
    private String reportType; // Critical, Urgent, General

    @Column(name = "concerned_office")
    private String concernedOffice; // Automatically determined based on the description

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(name = "user_idnumber")
    private String userIdNumber;

    @Column(name = "user_fullname")
    private String userFullName;

    @Column(name = "latitude")
    private Double latitude;  // Allow latitude to be nullable

    @Column(name = "longitude")
    private Double longitude; // Allow longitude to be nullable

    @Column(name = "requires_review", nullable = false)
    private boolean requiresReview;

    // No-argument constructor
    public ReportEntity() {}

    // Constructor with all parameters
    public ReportEntity(String description, String location, String image1Path, String image2Path, String image3Path,
                        String reportType, String concernedOffice, UserEntity user, LocalDateTime submittedAt,
                        ReportStatus status, boolean requiresReview, Double latitude, Double longitude) {
        this.description = description;
        this.location = location;
        this.image1Path = image1Path;
        this.image2Path = image2Path;
        this.image3Path = image3Path;
        this.reportType = reportType;
        this.concernedOffice = concernedOffice;
        this.user = user;
        this.submittedAt = submittedAt;
        this.status = status;
        this.userIdNumber = user.getIdNumber();
        this.userFullName = user.getFullName();
        this.requiresReview = requiresReview;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage1Path() {
        return image1Path;
    }

    public void setImage1Path(String image1Path) {
        this.image1Path = image1Path;
    }

    public String getImage2Path() {
        return image2Path;
    }

    public void setImage2Path(String image2Path) {
        this.image2Path = image2Path;
    }

    public String getImage3Path() {
        return image3Path;
    }

    public void setImage3Path(String image3Path) {
        this.image3Path = image3Path;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getConcernedOffice() {
        return concernedOffice;
    }

    public void setConcernedOffice(String concernedOffice) {
        this.concernedOffice = concernedOffice;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getUserIdNumber() {
        return userIdNumber;
    }

    public void setUserIdNumber(String userIdNumber) {
        this.userIdNumber = userIdNumber;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public boolean isRequiresReview() {
        return requiresReview;
    }

    public void setRequiresReview(boolean requiresReview) {
        this.requiresReview = requiresReview;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}

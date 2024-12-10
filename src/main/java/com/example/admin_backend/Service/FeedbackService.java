package com.example.admin_backend.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.admin_backend.Entity.FeedbackEntity;
import com.example.admin_backend.Entity.ReportEntity;
import com.example.admin_backend.Repository.FeedbackRepository;

@Service
public class FeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);
    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Transactional
    public FeedbackEntity createFeedbackForReport(ReportEntity report) {
        validateReport(report);

        Integer userId = report.getUserId();
        try {
            List<FeedbackEntity> feedbackList = feedbackRepository.findByUserIdOrderBySubmissionDateDesc(userId);

            FeedbackEntity feedback = buildFeedbackEntity(report, feedbackList.size() + 1);

            FeedbackEntity savedFeedback = feedbackRepository.save(feedback);
            logger.info("Created feedback for user {} with report ID {}", userId, report.getReportId());
            return savedFeedback;
        } catch (Exception e) {
            logger.error("Error creating feedback for report {}", report.getReportId(), e);
            throw new RuntimeException("Failed to create feedback", e);
        }
    }

    private void validateReport(ReportEntity report) {
        if (report == null) {
            throw new IllegalArgumentException("Report cannot be null");
        }

        if (report.getUserId() == null) {
            throw new IllegalArgumentException("Report must have a user ID");
        }
    }

    private FeedbackEntity buildFeedbackEntity(ReportEntity report, int totalReports) {
        FeedbackEntity feedback = new FeedbackEntity();
        feedback.setUserId(report.getUserId());
        feedback.setTotalReports(totalReports);

        // Set submission date
        LocalDateTime submittedAt = report.getSubmittedAt();
        feedback.setSubmissionDate(submittedAt != null ? submittedAt.toLocalDate() : LocalDate.now());

        // Set status
        feedback.setStatus(Objects.toString(report.getStatus(), "PENDING"));

        // Set category and location
        feedback.setReportCategory(report.getBuildingName());
        feedback.setLocation(formatLocation(report.getLatitude(), report.getLongitude()));

        // Set resolved date
        LocalDateTime resolvedAt = report.getResolvedAt();
        if (resolvedAt != null) {
            feedback.setDateResolved(resolvedAt.toLocalDate());
        }

        return feedback;
    }

    private String formatLocation(Double latitude, Double longitude) {
        double lat = latitude != null ? latitude : 0.0;
        double lon = longitude != null ? longitude : 0.0;
        return String.format("%.6f, %.6f", lat, lon);
    }

    public List<FeedbackEntity> getFeedbackForUser(int userId) {
        try {
            List<FeedbackEntity> feedbackList = feedbackRepository.findByUserIdOrderBySubmissionDateDesc(userId);
            logger.debug("Found {} feedback entries for user {}", feedbackList.size(), userId);
            return feedbackList;
        } catch (Exception e) {
            logger.error("Error fetching feedback for user {}", userId, e);
            throw new RuntimeException("Failed to fetch user feedback", e);
        }
    }

    public FeedbackEntity getLatestFeedbackForUser(int userId) {
        try {
            return feedbackRepository.findByUserIdOrderBySubmissionDateDesc(userId).stream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            logger.error("Error fetching latest feedback for user {}", userId, e);
            throw new RuntimeException("Failed to fetch latest feedback", e);
        }
    }

    public int getTotalReportsByUserId(int userId) {
        try {
            int count = feedbackRepository.countByUserId(userId);
            logger.debug("Found {} total reports for user {}", count, userId);
            return count;
        } catch (Exception e) {
            logger.error("Error counting reports for user {}", userId, e);
            throw new RuntimeException("Failed to count user reports", e);
        }
    }
}

package com.example.admin_backend.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.admin_backend.Entity.FeedbackEntity;
import com.example.admin_backend.Entity.ReportEntity;
import com.example.admin_backend.Repository.FeedbackRepository;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    // Create a new feedback entry
    public FeedbackEntity createFeedbackForReport(ReportEntity report) {
        int userId = report.getUser().getUserId();

        // Fetch existing feedback for the user
        List<FeedbackEntity> feedbackList = feedbackRepository.findByUserIdOrderBySubmissionDateDesc(userId);

        // Increment total reports for the new row
        int totalReports = feedbackList.size() + 1;

        // Create a new feedback entity
        FeedbackEntity feedback = new FeedbackEntity();
        feedback.setUserId(userId);
        feedback.setTotalReports(totalReports);
        feedback.setSubmissionDate(report.getSubmittedAt().toLocalDate());
        feedback.setStatus(report.getStatus().toString());
        feedback.setReportCategory(report.getReportType());
        feedback.setLocation(report.getLocation());
        feedback.setDateResolved(null); // Not resolved yet

        return feedbackRepository.save(feedback);
    }

    // Fetch all feedback for a user
    public List<FeedbackEntity> getFeedbackForUser(int userId) {
        return feedbackRepository.findByUserIdOrderBySubmissionDateDesc(userId);
    }

    // Fetch the latest feedback for a user
    public FeedbackEntity getLatestFeedbackForUser(int userId) {
        List<FeedbackEntity> feedbackList = feedbackRepository.findByUserIdOrderBySubmissionDateDesc(userId);
        return feedbackList.isEmpty() ? null : feedbackList.get(0);
    }

    // Count total feedback entries for a user
    public int getTotalReportsByUserId(int userId) {
        return feedbackRepository.countByUserId(userId);
    }
}

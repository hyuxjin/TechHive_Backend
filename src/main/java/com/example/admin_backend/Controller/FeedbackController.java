package com.example.admin_backend.Controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.admin_backend.Entity.FeedbackEntity;
import com.example.admin_backend.Service.FeedbackService;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // Get all feedback for a specific user
    @GetMapping("/latest/{userId}")
    public ResponseEntity<List<FeedbackEntity>> getFeedbackForUser(@PathVariable int userId) {
        List<FeedbackEntity> feedbackList = feedbackService.getFeedbackForUser(userId);
        if (feedbackList == null || feedbackList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(feedbackList);
    }

    // Get the total reports for a specific user
    @GetMapping("/totalReports/{userId}")
    public ResponseEntity<?> getTotalReports(@PathVariable int userId) {
        int totalReports = feedbackService.getTotalReportsByUserId(userId);
        return ResponseEntity.ok(Collections.singletonMap("totalReports", totalReports));
    }
}

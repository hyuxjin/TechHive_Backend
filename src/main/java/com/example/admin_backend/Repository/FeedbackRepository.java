package com.example.admin_backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.admin_backend.Entity.FeedbackEntity;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Integer> {

    // Fetch all feedback entries for a user, ordered by submission date (latest first)
    List<FeedbackEntity> findByUserIdOrderBySubmissionDateDesc(int userId);

    // Count the total number of feedback entries for a user (optional if needed later)
    int countByUserId(int userId);
}

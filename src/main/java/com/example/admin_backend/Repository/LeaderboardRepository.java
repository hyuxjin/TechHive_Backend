package com.example.admin_backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.admin_backend.Entity.LeaderboardEntity;

public interface LeaderboardRepository extends JpaRepository<LeaderboardEntity, Integer> {

    // Fetch leaderboard entries by userId
    Optional<LeaderboardEntity> findByUser_UserId(int userId);

    // Fetch all leaderboard entries ordered by points in descending order, then by achievedAt for tie-breaking
    List<LeaderboardEntity> findAllByOrderByPointsDescAchievedAtAsc();

    // Fetch the top 10 users ordered by points for leaderboard display
    List<LeaderboardEntity> findTop10ByOrderByPointsDescAchievedAtAsc();
}

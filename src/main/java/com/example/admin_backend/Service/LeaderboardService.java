package com.example.admin_backend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.admin_backend.Entity.LeaderboardEntity;
import com.example.admin_backend.Entity.UserEntity;
import com.example.admin_backend.Repository.LeaderboardRepository;
import com.example.admin_backend.Repository.UserRepository;

@Service
public class LeaderboardService {

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a new leaderboard entry for a user with the given points
    @Transactional
    public void createInitialLeaderboardEntry(int userId, int points) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found"));

        LeaderboardEntity leaderboardEntry = new LeaderboardEntity();
        leaderboardEntry.setUser(user);
        leaderboardEntry.setPoints(points);
        leaderboardEntry.setAchievedAt(LocalDateTime.now());
        leaderboardEntry.setUserRank(0);  // Rank will be updated later

        leaderboardRepository.save(leaderboardEntry);
        updateLeaderboardRanks(); // Auto-update leaderboard ranks
    }

    // Add points to the leaderboard for a user
    @Transactional
    public LeaderboardEntity addPoints(int userId, int points) {
        LeaderboardEntity leaderboardEntry = leaderboardRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new NoSuchElementException("Leaderboard entry not found for user"));

        leaderboardEntry.setPoints(leaderboardEntry.getPoints() + points);
        leaderboardEntry.setAchievedAt(LocalDateTime.now());

        leaderboardRepository.save(leaderboardEntry);
        updateLeaderboardRanks(); // Auto-update leaderboard ranks

        return leaderboardEntry;
    }

    // Get leaderboard rankings (sorted by points and time of achievement)
    public List<LeaderboardEntity> getLeaderboardRankings() {
        return leaderboardRepository.findAllByOrderByPointsDescAchievedAtAsc(); // Sorted by points, then by achievedAt
    }

    // Subtract points from the leaderboard for a user
    @Transactional
    public LeaderboardEntity subtractPoints(int userId, int points) {
        LeaderboardEntity leaderboardEntry = leaderboardRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new NoSuchElementException("Leaderboard entry not found for user"));

        leaderboardEntry.setPoints(Math.max(leaderboardEntry.getPoints() - points, 0)); // Prevent points from going below 0
        leaderboardEntry.setAchievedAt(LocalDateTime.now());

        leaderboardRepository.save(leaderboardEntry);
        updateLeaderboardRanks(); // Auto-update leaderboard ranks

        return leaderboardEntry;
    }

    // Get a specific user's leaderboard entry
    public LeaderboardEntity getLeaderboardEntryByUserId(int userId) {
        return leaderboardRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new NoSuchElementException("Leaderboard entry not found for user"));
    }

    // Method to automatically update leaderboard ranks
    @Transactional
    public void updateLeaderboardRanks() {
        List<LeaderboardEntity> leaderboard = leaderboardRepository.findAllByOrderByPointsDescAchievedAtAsc();
        int rank = 1;
        for (LeaderboardEntity entry : leaderboard) {
            entry.setUserRank(rank);
            leaderboardRepository.save(entry);
            rank++;
        }
    }

    // Helper method to determine badge based on points
    public String getBadge(int points) {
        if (points >= 100) {
            return "Champion";
        } else if (points >= 80) {
            return "Prowler";
        } else {
            return "Cub";
        }
    }
}

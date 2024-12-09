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

    @Transactional
    public void createInitialLeaderboardEntry(int userId, int points) {
        try {
            System.out.println("Creating initial leaderboard entry for user " + userId);
            UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

            LeaderboardEntity leaderboardEntry = new LeaderboardEntity();
            leaderboardEntry.setUser(user);
            leaderboardEntry.setPoints(points);
            leaderboardEntry.setAchievedAt(LocalDateTime.now());
            leaderboardEntry.setUserRank(0);

            leaderboardRepository.save(leaderboardEntry);
            updateLeaderboardRanks();
            System.out.println("Initial leaderboard entry created successfully");
        } catch (Exception e) {
            System.err.println("Error creating initial leaderboard entry: " + e.getMessage());
            throw new RuntimeException("Failed to create initial leaderboard entry", e);
        }
    }

    @Transactional
    public LeaderboardEntity addPoints(int userId, int points) {
        try {
            System.out.println("Adding " + points + " points to user " + userId);
            UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

            LeaderboardEntity leaderboardEntry = leaderboardRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    LeaderboardEntity newEntry = new LeaderboardEntity();
                    newEntry.setUser(user);
                    newEntry.setPoints(0);
                    newEntry.setUserRank(0);
                    newEntry.setAchievedAt(LocalDateTime.now());
                    return newEntry;
                });

            leaderboardEntry.setPoints(leaderboardEntry.getPoints() + points);
            leaderboardEntry.setAchievedAt(LocalDateTime.now());

            LeaderboardEntity savedEntry = leaderboardRepository.save(leaderboardEntry);
            updateLeaderboardRanks();

            System.out.println("Points added successfully. New total: " + savedEntry.getPoints());
            return savedEntry;
        } catch (Exception e) {
            System.err.println("Error adding points: " + e.getMessage());
            throw new RuntimeException("Failed to add points", e);
        }
    }

    @Transactional
    public LeaderboardEntity subtractPoints(int userId, int points) {
        try {
            System.out.println("Subtracting " + points + " points from user " + userId);
            UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

            LeaderboardEntity leaderboardEntry = leaderboardRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    LeaderboardEntity newEntry = new LeaderboardEntity();
                    newEntry.setUser(user);
                    newEntry.setPoints(0);
                    newEntry.setUserRank(0);
                    newEntry.setAchievedAt(LocalDateTime.now());
                    return newEntry;
                });

            int newPoints = Math.max(0, leaderboardEntry.getPoints() - points);
            leaderboardEntry.setPoints(newPoints);
            leaderboardEntry.setAchievedAt(LocalDateTime.now());

            LeaderboardEntity savedEntry = leaderboardRepository.save(leaderboardEntry);
            updateLeaderboardRanks();

            System.out.println("Points subtracted successfully. New total: " + savedEntry.getPoints());
            return savedEntry;
        } catch (Exception e) {
            System.err.println("Error subtracting points: " + e.getMessage());
            throw new RuntimeException("Failed to subtract points", e);
        }
    }

    public List<LeaderboardEntity> getLeaderboardRankings() {
        try {
            System.out.println("Fetching leaderboard rankings");
            List<LeaderboardEntity> rankings = leaderboardRepository.findAllByOrderByPointsDescAchievedAtAsc();
            System.out.println("Found " + rankings.size() + " leaderboard entries");
            return rankings;
        } catch (Exception e) {
            System.err.println("Error fetching leaderboard rankings: " + e.getMessage());
            throw new RuntimeException("Failed to fetch leaderboard rankings", e);
        }
    }

    public LeaderboardEntity getLeaderboardEntryByUserId(int userId) {
        try {
            System.out.println("Fetching leaderboard entry for user " + userId);
            LeaderboardEntity entry = leaderboardRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Leaderboard entry not found for user"));
            System.out.println("Found leaderboard entry with rank " + entry.getUserRank());
            return entry;
        } catch (Exception e) {
            System.err.println("Error fetching leaderboard entry: " + e.getMessage());
            throw new RuntimeException("Failed to fetch leaderboard entry", e);
        }
    }

    @Transactional
    public void updateLeaderboardRanks() {
        try {
            System.out.println("Updating leaderboard ranks");
            List<LeaderboardEntity> leaderboard = leaderboardRepository.findAllByOrderByPointsDescAchievedAtAsc();
            
            int currentRank = 1;
            int previousPoints = -1;
            int sameRankCount = 0;
            
            for (LeaderboardEntity entry : leaderboard) {
                if (entry.getPoints() == previousPoints) {
                    sameRankCount++;
                } else {
                    currentRank += sameRankCount;
                    sameRankCount = 0;
                }
                
                entry.setUserRank(currentRank);
                leaderboardRepository.save(entry);
                previousPoints = entry.getPoints();
            }
            System.out.println("Leaderboard ranks updated successfully");
        } catch (Exception e) {
            System.err.println("Error updating leaderboard ranks: " + e.getMessage());
            throw new RuntimeException("Failed to update leaderboard ranks", e);
        }
    }

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
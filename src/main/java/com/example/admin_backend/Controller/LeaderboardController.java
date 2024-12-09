package com.example.admin_backend.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.admin_backend.Entity.LeaderboardEntity;
import com.example.admin_backend.Service.LeaderboardService;

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "http://localhost:3000")
public class LeaderboardController {
    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping("/rankings")
    public ResponseEntity<?> getLeaderboardRankings() {
        try {
            System.out.println("Received request for leaderboard rankings");
            List<LeaderboardEntity> rankings = leaderboardService.getLeaderboardRankings();
            return ResponseEntity.ok(rankings);
        } catch (Exception e) {
            System.err.println("Error in getLeaderboardRankings: " + e.getMessage());
            return ResponseEntity.status(500)
                .body("Failed to fetch leaderboard rankings: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getLeaderboardEntryByUserId(@PathVariable int userId) {
        try {
            System.out.println("Received request for user " + userId + "'s leaderboard entry");
            LeaderboardEntity entry = leaderboardService.getLeaderboardEntryByUserId(userId);
            return ResponseEntity.ok(entry);
        } catch (Exception e) {
            System.err.println("Error in getLeaderboardEntryByUserId: " + e.getMessage());
            return ResponseEntity.status(500)
                .body("Failed to fetch leaderboard entry: " + e.getMessage());
        }
    }

    @PostMapping("/addPoints")
    public ResponseEntity<?> addPointsToUser(
            @RequestParam int userId,
            @RequestParam int points) {
        try {
            System.out.println("Received request to add " + points + " points to user " + userId);
            LeaderboardEntity result = leaderboardService.addPoints(userId, points);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error in addPointsToUser: " + e.getMessage());
            return ResponseEntity.status(500)
                .body("Failed to add points: " + e.getMessage());
        }
    }

    @PostMapping("/subtractPoints")
    public ResponseEntity<?> subtractPointsFromUser(
            @RequestParam int userId,
            @RequestParam int points) {
        try {
            System.out.println("Received request to subtract " + points + " points from user " + userId);
            LeaderboardEntity result = leaderboardService.subtractPoints(userId, points);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error in subtractPointsFromUser: " + e.getMessage());
            return ResponseEntity.status(500)
                .body("Failed to subtract points: " + e.getMessage());
        }
    }

    @PutMapping("/updateRanks")
    public ResponseEntity<?> updateLeaderboardRanks() {
        try {
            System.out.println("Received request to update leaderboard ranks");
            leaderboardService.updateLeaderboardRanks();
            return ResponseEntity.ok("Leaderboard ranks updated successfully");
        } catch (Exception e) {
            System.err.println("Error in updateLeaderboardRanks: " + e.getMessage());
            return ResponseEntity.status(500)
                .body("Failed to update ranks: " + e.getMessage());
        }
    }

    @GetMapping("/badge/{points}")
    public ResponseEntity<?> getBadge(@PathVariable int points) {
        try {
            System.out.println("Received request for badge calculation for " + points + " points");
            String badge = leaderboardService.getBadge(points);
            return ResponseEntity.ok(badge);
        } catch (Exception e) {
            System.err.println("Error in getBadge: " + e.getMessage());
            return ResponseEntity.status(500)
                .body("Failed to get badge: " + e.getMessage());
        }
    }
}
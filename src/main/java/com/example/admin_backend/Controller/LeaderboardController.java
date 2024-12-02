package com.example.admin_backend.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.admin_backend.Entity.LeaderboardEntity;
import com.example.admin_backend.Service.LeaderboardService;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    // Get the leaderboard rankings (ordered by points)
    @GetMapping("/rankings")
    public ResponseEntity<List<LeaderboardEntity>> getLeaderboardRankings() {
        List<LeaderboardEntity> rankings = leaderboardService.getLeaderboardRankings();
        return ResponseEntity.ok(rankings);
    }

    // Get the leaderboard entry for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<LeaderboardEntity> getLeaderboardEntryByUserId(@PathVariable int userId) {
        LeaderboardEntity leaderboardEntry = leaderboardService.getLeaderboardEntryByUserId(userId);
        return ResponseEntity.ok(leaderboardEntry);
    }

    // Add points to a user's leaderboard entry
    @PostMapping("/addPoints")
    public ResponseEntity<LeaderboardEntity> addPointsToUser(@RequestParam int userId, @RequestParam int points) {
        LeaderboardEntity leaderboardEntry = leaderboardService.addPoints(userId, points);
        return ResponseEntity.ok(leaderboardEntry);
    }

    // Subtract points from a user's leaderboard entry
    @PostMapping("/subtractPoints")
    public ResponseEntity<LeaderboardEntity> subtractPointsFromUser(@RequestParam int userId, @RequestParam int points) {
        LeaderboardEntity leaderboardEntry = leaderboardService.subtractPoints(userId, points);
        return ResponseEntity.ok(leaderboardEntry);
    }

    // Endpoint to update leaderboard ranks
    @PutMapping("/updateRanks")
    public ResponseEntity<String> updateLeaderboardRanks() {
        leaderboardService.updateLeaderboardRanks();
        return ResponseEntity.ok("Leaderboard ranks updated successfully.");
    }
}

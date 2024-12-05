package com.example.admin_backend.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblleaderboard")
public class LeaderboardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leaderboard_id")
    private int leaderboardId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "achieved_at", nullable = false)
    private LocalDateTime achievedAt;

    @Column(name = "user_rank", nullable = false)
    private int userRank;

    public LeaderboardEntity() {}

    public LeaderboardEntity(UserEntity user, int points, LocalDateTime achievedAt, int userRank) {
        this.user = user;
        this.points = points;
        this.achievedAt = achievedAt;
        this.userRank = userRank;
    }

    public int getLeaderboardId() {
        return leaderboardId;
    }

    public void setLeaderboardId(int leaderboardId) {
        this.leaderboardId = leaderboardId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public LocalDateTime getAchievedAt() {
        return achievedAt;
    }

    public void setAchievedAt(LocalDateTime achievedAt) {
        this.achievedAt = achievedAt;
    }

    public int getUserRank() {
        return userRank;
    }

    public void setUserRank(int userRank) {
        this.userRank = userRank;
    }
}

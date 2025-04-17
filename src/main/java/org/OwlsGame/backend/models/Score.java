package org.OwlsGame.backend.models;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "scores")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "game_id", nullable = false)
    private int gameId;

    @Column(name = "score_value", nullable = false)
    private int scoreValue;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "play_time", nullable = false)
    private int playTime; // 单位：秒

    // Constructors
    public Score() {}

    public Score(int userId, int gameId, int scoreValue, Timestamp timestamp, String email, int playTime) {
        this.userId = userId;
        this.gameId = gameId;
        this.scoreValue = scoreValue;
        this.timestamp = timestamp;
        this.email = email;
        this.playTime = playTime;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getGameId() {
        return gameId;
    }
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
    public int getScoreValue() {
        return scoreValue;
    }
    public void setScoreValue(int scoreValue) {
        this.scoreValue = scoreValue;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public int getPlayTime() {
        return playTime;
    }
    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }
}
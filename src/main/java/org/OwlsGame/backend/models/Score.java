package org.OwlsGame.backend.models;



import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "scores")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Unique identifier for the score record

    @Column(name = "user_id", nullable = false)
    private int userId; // ID of the user who achieved the score

    @Column(name = "game_id", nullable = false)
    private int gameId; // ID of the game for which the score was achieved

    @Column(name = "score_value", nullable = false)
    private int scoreValue; // The actual score value achieved

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp; // The time when the score was recorded

    @Column(name = "email", nullable = false)
    private String email; // Email of the user who achieved the score

    @Column(name = "play_time", nullable = false)
    private int playTime; // The play time for the game

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
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
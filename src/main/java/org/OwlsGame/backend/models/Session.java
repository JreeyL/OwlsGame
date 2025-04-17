package org.OwlsGame.backend.models;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", unique = true, nullable = false)
    private String sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "creation_time", nullable = false)
    private Timestamp creationTime;

    @Column(name = "last_accessed_time", nullable = false)
    private Timestamp lastAccessedTime;

    @Column(name = "is_valid", nullable = false)
    private boolean isValid = true;

    @Column(name = "last_played_game_id")
    private Long lastPlayedGameId;

    @Column(name = "favorite_game_id")
    private Long favoriteGameId;

    @Column(name = "cumulative_score")
    private int cumulativeScore;

    // 可选：如需临时存储业务属性，但不持久化
    @Transient
    private Map<String, Object> attributes = new HashMap<>();

    public Session() {}

    public Session(String sessionId, Long userId, Timestamp creationTime) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.creationTime = creationTime;
        this.lastAccessedTime = creationTime;
        this.isValid = true;
    }

    // ----------- Getters & Setters -----------

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getSessionId() { return sessionId; }

    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public Timestamp getCreationTime() { return creationTime; }

    public void setCreationTime(Timestamp creationTime) { this.creationTime = creationTime; }

    public Timestamp getLastAccessedTime() { return lastAccessedTime; }

    public void setLastAccessedTime(Timestamp lastAccessedTime) { this.lastAccessedTime = lastAccessedTime; }

    public boolean isValid() { return isValid; }

    public void setValid(boolean valid) { isValid = valid; }

    public Long getLastPlayedGameId() { return lastPlayedGameId; }

    public void setLastPlayedGameId(Long lastPlayedGameId) { this.lastPlayedGameId = lastPlayedGameId; }

    public Long getFavoriteGameId() { return favoriteGameId; }

    public void setFavoriteGameId(Long favoriteGameId) { this.favoriteGameId = favoriteGameId; }

    public int getCumulativeScore() { return cumulativeScore; }

    public void setCumulativeScore(int cumulativeScore) { this.cumulativeScore = cumulativeScore; }

    public Map<String, Object> getAttributes() { return attributes; }

    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }

    // ----------- 业务方法 -----------

    public void invalidate() { this.isValid = false; }

    public void setAttribute(String key, Object value) { attributes.put(key, value); }

    public Object getAttribute(String key) { return attributes.get(key); }

    public void removeAttribute(String key) { attributes.remove(key); }
}
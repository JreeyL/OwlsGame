package org.OwlsGame.backend.models;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Session {
    private String sessionId;
    private int userId;
    private Timestamp creationTime;
    private Timestamp lastAccessedTime;
    private boolean isValid;
    private Map<String, Object> attributes;

    // Constructors
    public Session() {
        this.attributes = new HashMap<>();
        this.isValid = true;
    }

    public Session(String sessionId, int userId, Timestamp creationTime) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.creationTime = creationTime;
        this.lastAccessedTime = creationTime;
        this.isValid = true;
        this.attributes = new HashMap<>();
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public Timestamp getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(Timestamp lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // Methods
    public void invalidate() {
        this.isValid = false;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
    }
}
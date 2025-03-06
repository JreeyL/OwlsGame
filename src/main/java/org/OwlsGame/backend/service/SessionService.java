package org.OwlsGame.backend.service;

import org.OwlsGame.backend.models.Session;

import java.util.List;

public interface SessionService {
    void createSession(Session session);
    Session getSessionById(int id);
    List<Session> getAllSessions();
    void updateSession(Session session);
    void deleteSessionById(int id);

    // New methods for tracking user activities
    void trackLastPlayedGame(int sessionId, int gameId);
    void trackFavoriteGame(int sessionId, int gameId);
    void updateCumulativeScore(int sessionId, int score);
}
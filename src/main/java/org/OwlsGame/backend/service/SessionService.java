package org.OwlsGame.backend.service;

import org.OwlsGame.backend.models.Session;

import java.util.List;
import java.util.Optional;

public interface SessionService {
    void createSession(Session session);
    Optional<Session> getSessionById(Long id);
    List<Session> getAllSessions();
    void updateSession(Session session);
    void deleteSessionById(Long id);
    void trackLastPlayedGame(Long sessionId, Long gameId);
    void trackFavoriteGame(Long sessionId, Long gameId);
    void updateCumulativeScore(Long sessionId, int score);
    Optional<Session> findBySessionId(String sessionId);
    List<Session> findByUserId(Long userId);
}
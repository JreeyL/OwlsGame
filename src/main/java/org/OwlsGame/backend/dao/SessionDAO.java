package org.OwlsGame.backend.dao;

import org.OwlsGame.backend.models.Session;

import java.sql.SQLException;
import java.util.List;

public interface SessionDAO {
    void addSession(Session session) throws SQLException;
    Session getSessionById(int id) throws SQLException;
    List<Session> getAllSessions() throws SQLException;
    void updateSession(Session session) throws SQLException;
    void deleteSessionById(int id) throws SQLException;

    // New methods for tracking user activities
    void updateLastPlayedGame(int sessionId, int gameId) throws SQLException;
    void updateFavoriteGame(int sessionId, int gameId) throws SQLException;
    void updateCumulativeScore(int sessionId, int score) throws SQLException;
}
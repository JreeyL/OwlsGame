package org.OwlsGame.backend.service;

import org.OwlsGame.backend.dao.SessionDAO;
import org.OwlsGame.backend.models.Session;

import java.sql.SQLException;
import java.util.List;

public class SessionServiceImpl implements SessionService {

    private SessionDAO sessionDAO;

    @Override
    public void createSession(Session session) {
        try {
            sessionDAO.addSession(session);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Session getSessionById(int id) {
        Session session = null;
        try {
            session = sessionDAO.getSessionById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return session;
    }

    @Override
    public List<Session> getAllSessions() {
        List<Session> sessions = null;
        try {
            sessions = sessionDAO.getAllSessions();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    @Override
    public void updateSession(Session session) {
        try {
            sessionDAO.updateSession(session);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSessionById(int id) {
        try {
            sessionDAO.deleteSessionById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Implement new methods for tracking user activities
    @Override
    public void trackLastPlayedGame(int sessionId, int gameId) {
        try {
            sessionDAO.updateLastPlayedGame(sessionId, gameId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void trackFavoriteGame(int sessionId, int gameId) {
        try {
            sessionDAO.updateFavoriteGame(sessionId, gameId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCumulativeScore(int sessionId, int score) {
        try {
            sessionDAO.updateCumulativeScore(sessionId, score);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
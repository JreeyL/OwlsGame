package org.OwlsGame.backend.service;

import org.OwlsGame.backend.dao.SessionRepository;
import org.OwlsGame.backend.models.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Autowired
    public SessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void createSession(Session session) {
        sessionRepository.save(session);
    }

    @Override
    public Optional<Session> getSessionById(Long id) {
        return sessionRepository.findById(id);
    }

    @Override
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    @Override
    public void updateSession(Session session) {
        sessionRepository.save(session);
    }

    @Override
    public void deleteSessionById(Long id) {
        sessionRepository.deleteById(id);
    }

    @Override
    public void trackLastPlayedGame(Long sessionId, Long gameId) {
        sessionRepository.updateLastPlayedGame(sessionId, gameId);
    }

    @Override
    public void trackFavoriteGame(Long sessionId, Long gameId) {
        sessionRepository.updateFavoriteGame(sessionId, gameId);
    }

    @Override
    public void updateCumulativeScore(Long sessionId, int score) {
        sessionRepository.updateCumulativeScore(sessionId, score);
    }

    @Override
    public Optional<Session> findBySessionId(String sessionId) {
        return sessionRepository.findBySessionId(sessionId);
    }

    @Override
    public List<Session> findByUserId(Long userId) {
        return sessionRepository.findByUserId(userId);
    }
}
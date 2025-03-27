package org.OwlsGame.backend.service;

import org.OwlsGame.backend.dao.SessionRepository;
import org.OwlsGame.backend.models.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionServiceImpl sessionService;

    private Session testSession;
    private Session anotherSession;
    private final Long sessionId = 1L;
    private final Long userId = 101L;
    private final Long gameId = 201L;

    @BeforeEach
    void setUp() {
        // 创建测试用的会话对象
        String sessionIdString = UUID.randomUUID().toString();
        Timestamp now = Timestamp.from(Instant.now());

        testSession = new Session(sessionIdString, userId, now);
        testSession.setId(sessionId);
        testSession.setLastPlayedGameId(null);
        testSession.setFavoriteGameId(null);
        testSession.setCumulativeScore(0);

        anotherSession = new Session(UUID.randomUUID().toString(), userId, now);
        anotherSession.setId(2L);
        anotherSession.setCumulativeScore(100);
    }

    @Test
    void whenCreateSession_thenSessionRepositorySaveIsCalled() {
        // given
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

        // when
        sessionService.createSession(testSession);

        // then
        verify(sessionRepository, times(1)).save(testSession);
    }

    @Test
    void whenGetSessionById_thenReturnSession() {
        // given
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));

        // when
        Optional<Session> foundSession = sessionService.getSessionById(sessionId);

        // then
        assertTrue(foundSession.isPresent());
        assertEquals(sessionId, foundSession.get().getId());
        verify(sessionRepository, times(1)).findById(sessionId);
    }

    @Test
    void whenGetSessionByNonExistingId_thenReturnEmpty() {
        // given
        Long nonExistingId = 999L;
        when(sessionRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // when
        Optional<Session> foundSession = sessionService.getSessionById(nonExistingId);

        // then
        assertFalse(foundSession.isPresent());
        verify(sessionRepository, times(1)).findById(nonExistingId);
    }

    @Test
    void whenGetAllSessions_thenReturnList() {
        // given
        List<Session> sessions = Arrays.asList(testSession, anotherSession);
        when(sessionRepository.findAll()).thenReturn(sessions);

        // when
        List<Session> allSessions = sessionService.getAllSessions();

        // then
        assertEquals(2, allSessions.size());
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    void whenUpdateSession_thenSessionRepositorySaveIsCalled() {
        // given
        testSession.setCumulativeScore(50);
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

        // when
        sessionService.updateSession(testSession);

        // then
        verify(sessionRepository, times(1)).save(testSession);
    }

    @Test
    void whenDeleteSessionById_thenSessionRepositoryDeleteByIdIsCalled() {
        // given
        doNothing().when(sessionRepository).deleteById(sessionId);

        // when
        sessionService.deleteSessionById(sessionId);

        // then
        verify(sessionRepository, times(1)).deleteById(sessionId);
    }

    @Test
    void whenTrackLastPlayedGame_thenSessionRepositoryUpdateLastPlayedGameIsCalled() {
        // given
        doNothing().when(sessionRepository).updateLastPlayedGame(sessionId, gameId);

        // when
        sessionService.trackLastPlayedGame(sessionId, gameId);

        // then
        verify(sessionRepository, times(1)).updateLastPlayedGame(sessionId, gameId);
    }

    @Test
    void whenTrackFavoriteGame_thenSessionRepositoryUpdateFavoriteGameIsCalled() {
        // given
        doNothing().when(sessionRepository).updateFavoriteGame(sessionId, gameId);

        // when
        sessionService.trackFavoriteGame(sessionId, gameId);

        // then
        verify(sessionRepository, times(1)).updateFavoriteGame(sessionId, gameId);
    }

    @Test
    void whenUpdateCumulativeScore_thenSessionRepositoryUpdateCumulativeScoreIsCalled() {
        // given
        int additionalScore = 50;
        doNothing().when(sessionRepository).updateCumulativeScore(sessionId, additionalScore);

        // when
        sessionService.updateCumulativeScore(sessionId, additionalScore);

        // then
        verify(sessionRepository, times(1)).updateCumulativeScore(sessionId, additionalScore);
    }

    @Test
    void whenFindBySessionId_thenReturnSession() {
        // given
        String sessionIdStr = testSession.getSessionId();
        when(sessionRepository.findBySessionId(sessionIdStr)).thenReturn(Optional.of(testSession));

        // when
        Optional<Session> foundSession = sessionService.findBySessionId(sessionIdStr);

        // then
        assertTrue(foundSession.isPresent());
        assertEquals(sessionIdStr, foundSession.get().getSessionId());
        verify(sessionRepository, times(1)).findBySessionId(sessionIdStr);
    }

    @Test
    void whenFindByNonExistingSessionId_thenReturnEmpty() {
        // given
        String nonExistingSessionId = "non-existing-session-id";
        when(sessionRepository.findBySessionId(nonExistingSessionId)).thenReturn(Optional.empty());

        // when
        Optional<Session> foundSession = sessionService.findBySessionId(nonExistingSessionId);

        // then
        assertFalse(foundSession.isPresent());
        verify(sessionRepository, times(1)).findBySessionId(nonExistingSessionId);
    }

    @Test
    void whenFindByUserId_thenReturnSessionsList() {
        // given
        List<Session> userSessions = Arrays.asList(testSession, anotherSession);
        when(sessionRepository.findByUserId(userId)).thenReturn(userSessions);

        // when
        List<Session> foundSessions = sessionService.findByUserId(userId);

        // then
        assertEquals(2, foundSessions.size());
        verify(sessionRepository, times(1)).findByUserId(userId);
    }

    @Test
    void whenFindByNonExistingUserId_thenReturnEmptyList() {
        // given
        Long nonExistingUserId = 999L;
        when(sessionRepository.findByUserId(nonExistingUserId)).thenReturn(List.of());

        // when
        List<Session> foundSessions = sessionService.findByUserId(nonExistingUserId);

        // then
        assertTrue(foundSessions.isEmpty());
        verify(sessionRepository, times(1)).findByUserId(nonExistingUserId);
    }

    @Test
    void whenTrackLastPlayedGameWithNullGameId_thenRepositoryStillCalled() {
        // given
        Long nullGameId = null;
        doNothing().when(sessionRepository).updateLastPlayedGame(sessionId, nullGameId);

        // when
        sessionService.trackLastPlayedGame(sessionId, nullGameId);

        // then
        verify(sessionRepository, times(1)).updateLastPlayedGame(sessionId, nullGameId);
    }

    @Test
    void whenTrackFavoriteGameWithNullGameId_thenRepositoryStillCalled() {
        // given
        Long nullGameId = null;
        doNothing().when(sessionRepository).updateFavoriteGame(sessionId, nullGameId);

        // when
        sessionService.trackFavoriteGame(sessionId, nullGameId);

        // then
        verify(sessionRepository, times(1)).updateFavoriteGame(sessionId, nullGameId);
    }
}
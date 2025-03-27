package org.OwlsGame.backend.dao;

import org.OwlsGame.backend.models.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.TestTransaction;

import jakarta.transaction.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SessionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SessionRepository sessionRepository;

    // 创建测试用会话的辅助方法
    private Session createTestSession(Long userId) {
        String sessionId = UUID.randomUUID().toString();
        Timestamp now = Timestamp.from(Instant.now());

        // 使用正确的构造函数创建 Session 对象
        Session session = new Session(sessionId, userId, now);

        // 设置其他字段
        session.setLastPlayedGameId(null);
        session.setFavoriteGameId(null);
        session.setCumulativeScore(0);

        return session;
    }

    @Test
    public void whenFindBySessionId_thenReturnSession() {
        // given
        Session session = createTestSession(1L);
        entityManager.persist(session);
        entityManager.flush();

        // when
        Optional<Session> found = sessionRepository.findBySessionId(session.getSessionId());

        // then
        assertTrue(found.isPresent());
        assertEquals(session.getSessionId(), found.get().getSessionId());
        assertEquals(session.getUserId(), found.get().getUserId());
    }

    @Test
    public void whenFindByNonExistingSessionId_thenReturnEmpty() {
        // given
        String nonExistingSessionId = UUID.randomUUID().toString();

        // when
        Optional<Session> found = sessionRepository.findBySessionId(nonExistingSessionId);

        // then
        assertFalse(found.isPresent());
    }

    @Test
    public void whenFindByUserId_thenReturnAllUserSessions() {
        // given
        Long userId = 2L;
        Session session1 = createTestSession(userId);
        Session session2 = createTestSession(userId);
        Session session3 = createTestSession(3L); // 不同用户的会话

        entityManager.persist(session1);
        entityManager.persist(session2);
        entityManager.persist(session3);
        entityManager.flush();

        // when
        List<Session> userSessions = sessionRepository.findByUserId(userId);

        // then
        assertEquals(2, userSessions.size());
        assertTrue(userSessions.stream().allMatch(s -> s.getUserId().equals(userId)));
    }

    @Test
    public void whenFindByNonExistingUserId_thenReturnEmptyList() {
        // given
        Long nonExistingUserId = 999L;

        // when
        List<Session> userSessions = sessionRepository.findByUserId(nonExistingUserId);

        // then
        assertTrue(userSessions.isEmpty());
    }

    @Test
    @Transactional
    public void whenUpdateLastPlayedGame_thenLastPlayedGameIsUpdated() {
        // given
        Session session = createTestSession(4L);
        session = entityManager.persist(session);
        entityManager.flush();

        Long gameId = 101L;

        // when
        sessionRepository.updateLastPlayedGame(session.getId(), gameId);
        entityManager.flush();
        entityManager.clear(); // 清除实体管理器缓存，确保从数据库重新加载

        // then
        Session updatedSession = entityManager.find(Session.class, session.getId());
        assertEquals(gameId, updatedSession.getLastPlayedGameId());
    }

    @Test
    @Transactional
    public void whenUpdateFavoriteGame_thenFavoriteGameIsUpdated() {
        // given
        Session session = createTestSession(5L);
        session = entityManager.persist(session);
        entityManager.flush();

        Long gameId = 202L;

        // when
        sessionRepository.updateFavoriteGame(session.getId(), gameId);
        entityManager.flush();
        entityManager.clear();

        // then
        Session updatedSession = entityManager.find(Session.class, session.getId());
        assertEquals(gameId, updatedSession.getFavoriteGameId());
    }

    @Test
    @Transactional
    public void whenUpdateCumulativeScore_thenScoreIsIncremented() {
        // given
        Session session = createTestSession(6L);
        session.setCumulativeScore(100);
        session = entityManager.persist(session);
        entityManager.flush();

        int additionalScore = 50;

        // when
        sessionRepository.updateCumulativeScore(session.getId(), additionalScore);
        entityManager.flush();
        entityManager.clear();

        // then
        Session updatedSession = entityManager.find(Session.class, session.getId());
        assertEquals(150, updatedSession.getCumulativeScore());
    }

    @Test
    @Transactional
    public void whenUpdateCumulativeScoreMultipleTimes_thenScoreIsAccumulated() {
        // given
        Session session = createTestSession(7L);
        session.setCumulativeScore(0);
        session = entityManager.persist(session);
        entityManager.flush();

        // when
        sessionRepository.updateCumulativeScore(session.getId(), 10);
        sessionRepository.updateCumulativeScore(session.getId(), 20);
        sessionRepository.updateCumulativeScore(session.getId(), 30);
        entityManager.flush();
        entityManager.clear();

        // then
        Session updatedSession = entityManager.find(Session.class, session.getId());
        assertEquals(60, updatedSession.getCumulativeScore());
    }

    @Test
    public void whenSaveSession_thenSessionIsPersisted() {
        // given
        Session session = createTestSession(8L);

        // when
        Session savedSession = sessionRepository.save(session);

        // then
        assertNotNull(savedSession.getId());
        assertEquals(session.getSessionId(), savedSession.getSessionId());
        assertEquals(session.getUserId(), savedSession.getUserId());
        assertTrue(savedSession.isValid());
        assertNotNull(savedSession.getCreationTime());
        assertNotNull(savedSession.getLastAccessedTime());
    }

    @Test
    public void whenDeleteSession_thenSessionIsRemoved() {
        // given
        Session session = createTestSession(9L);
        session = entityManager.persist(session);
        entityManager.flush();

        // when
        sessionRepository.deleteById(session.getId());
        entityManager.flush();

        // then
        Session deletedSession = entityManager.find(Session.class, session.getId());
        assertNull(deletedSession);
    }

    @Test
    public void whenInvalidateSession_thenIsValidIsFalse() {
        // given
        Session session = createTestSession(10L);
        session = entityManager.persist(session);
        entityManager.flush();

        // when
        session.invalidate();
        sessionRepository.save(session);
        entityManager.flush();
        entityManager.clear();

        // then
        Session updatedSession = entityManager.find(Session.class, session.getId());
        assertFalse(updatedSession.isValid());
    }

    @Test
    @Transactional
    public void whenUpdateAccessTime_thenLastAccessedTimeIsUpdated() {
        // given
        Session session = createTestSession(11L);
        entityManager.persist(session);
        entityManager.flush();

        // 获取原始时间戳
        Long originalTimeMillis = session.getLastAccessedTime().getTime();

        // 确保时间戳差异明显 - 等待更长时间
        try {
            Thread.sleep(100); // 等待100毫秒而不是10毫秒
        } catch (InterruptedException e) {
            // 忽略中断异常
        }

        // when - 创建一个明显不同的时间戳
        Timestamp newTime = new Timestamp(originalTimeMillis + 1000); // 增加1秒
        session.setLastAccessedTime(newTime);

        // 使用Repository保存更改
        Session updatedSession = sessionRepository.save(session);
        entityManager.flush();
        entityManager.clear();

        // then - 重新从数据库获取会话
        Session fetchedSession = entityManager.find(Session.class, session.getId());

        // 比较原始时间和更新后的时间
        assertNotNull(fetchedSession);
        assertNotEquals(originalTimeMillis, fetchedSession.getLastAccessedTime().getTime());
        assertEquals(newTime.getTime(), fetchedSession.getLastAccessedTime().getTime());
    }
}
package org.OwlsGame.backend.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    private Session session;
    private final String SESSION_ID = "test-session-123";
    private final Long USER_ID = 1L;
    private final Timestamp CREATION_TIME = Timestamp.from(Instant.now());

    @BeforeEach
    void setUp() {
        session = new Session(SESSION_ID, USER_ID, CREATION_TIME);
    }

    @Test
    void testDefaultConstructor() {
        Session defaultSession = new Session();
        assertTrue(defaultSession.isValid());
        assertNotNull(defaultSession.getAttributes());
        assertEquals(0, defaultSession.getAttributes().size());
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals(SESSION_ID, session.getSessionId());
        assertEquals(USER_ID, session.getUserId());
        assertEquals(CREATION_TIME, session.getCreationTime());
        assertEquals(CREATION_TIME, session.getLastAccessedTime());
        assertTrue(session.isValid());
        assertNotNull(session.getAttributes());
        assertEquals(0, session.getAttributes().size());
    }

    @Test
    void testIdGetterAndSetter() {
        Long id = 2L;
        session.setId(id);
        assertEquals(id, session.getId());
    }

    @Test
    void testSessionIdGetterAndSetter() {
        String newSessionId = "new-session-456";
        session.setSessionId(newSessionId);
        assertEquals(newSessionId, session.getSessionId());
    }

    @Test
    void testUserIdGetterAndSetter() {
        Long newUserId = 3L;
        session.setUserId(newUserId);
        assertEquals(newUserId, session.getUserId());
    }

    @Test
    void testCreationTimeGetterAndSetter() {
        Timestamp newCreationTime = new Timestamp(System.currentTimeMillis() - 10000);
        session.setCreationTime(newCreationTime);
        assertEquals(newCreationTime, session.getCreationTime());
    }

    @Test
    void testLastAccessedTimeGetterAndSetter() {
        Timestamp newLastAccessedTime = new Timestamp(System.currentTimeMillis());
        session.setLastAccessedTime(newLastAccessedTime);
        assertEquals(newLastAccessedTime, session.getLastAccessedTime());
    }

    @Test
    void testValidFlagGetterAndSetter() {
        session.setValid(false);
        assertFalse(session.isValid());
        session.setValid(true);
        assertTrue(session.isValid());
    }

    @Test
    void testLastPlayedGameIdGetterAndSetter() {
        Long lastPlayedGameId = 10L;
        session.setLastPlayedGameId(lastPlayedGameId);
        assertEquals(lastPlayedGameId, session.getLastPlayedGameId());
    }

    @Test
    void testFavoriteGameIdGetterAndSetter() {
        Long favoriteGameId = 20L;
        session.setFavoriteGameId(favoriteGameId);
        assertEquals(favoriteGameId, session.getFavoriteGameId());
    }

    @Test
    void testCumulativeScoreGetterAndSetter() {
        int score = 100;
        session.setCumulativeScore(score);
        assertEquals(score, session.getCumulativeScore());
    }

    @Test
    void testInvalidate() {
        assertTrue(session.isValid());
        session.invalidate();
        assertFalse(session.isValid());
    }

    @Test
    void testAttributeOperations() {
        // Initial state
        assertTrue(session.getAttributes().isEmpty());

        // set entity
        String key = "testKey";
        String value = "testValue";
        session.setAttribute(key, value);
        assertEquals(value, session.getAttribute(key));
        assertEquals(1, session.getAttributes().size());

        // update entity
        Integer newValue = 42;
        session.setAttribute(key, newValue);
        assertEquals(newValue, session.getAttribute(key));
        assertEquals(1, session.getAttributes().size());

        // add another entity
        String key2 = "anotherKey";
        Boolean value2 = true;
        session.setAttribute(key2, value2);
        assertEquals(value2, session.getAttribute(key2));
        assertEquals(2, session.getAttributes().size());

        // remove entity
        session.removeAttribute(key);
        assertNull(session.getAttribute(key));
        assertEquals(1, session.getAttributes().size());

        // remove entity which is not exist
        session.removeAttribute("nonExistentKey");
        assertEquals(1, session.getAttributes().size());
    }
}
package org.OwlsGame.backend.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ScoreTest {

    private Score score;
    private final int USER_ID = 1;
    private final int GAME_ID = 2;
    private final int SCORE_VALUE = 100;
    private final Timestamp TIMESTAMP = Timestamp.from(Instant.now());
    private final String EMAIL = "test@example.com";
    private final int PLAY_TIME = 300; // 300 seconds

    @BeforeEach
    void setUp() {
        score = new Score(USER_ID, GAME_ID, SCORE_VALUE, TIMESTAMP, EMAIL, PLAY_TIME);
    }

    @Test
    void testDefaultConstructor() {
        Score defaultScore = new Score();
        assertNotNull(defaultScore);
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals(USER_ID, score.getUserId());
        assertEquals(GAME_ID, score.getGameId());
        assertEquals(SCORE_VALUE, score.getScoreValue());
        assertEquals(TIMESTAMP, score.getTimestamp());
        assertEquals(EMAIL, score.getEmail());
        assertEquals(PLAY_TIME, score.getPlayTime());
    }

    @Test
    void testIdGetterAndSetter() {
        Integer id = 5;
        score.setId(id);
        assertEquals(id, score.getId());
    }

    @Test
    void testUserIdGetterAndSetter() {
        int newUserId = 10;
        score.setUserId(newUserId);
        assertEquals(newUserId, score.getUserId());
    }

    @Test
    void testGameIdGetterAndSetter() {
        int newGameId = 20;
        score.setGameId(newGameId);
        assertEquals(newGameId, score.getGameId());
    }

    @Test
    void testScoreValueGetterAndSetter() {
        int newScoreValue = 500;
        score.setScoreValue(newScoreValue);
        assertEquals(newScoreValue, score.getScoreValue());
    }

    @Test
    void testTimestampGetterAndSetter() {
        Timestamp newTimestamp = new Timestamp(System.currentTimeMillis() - 10000);
        score.setTimestamp(newTimestamp);
        assertEquals(newTimestamp, score.getTimestamp());
    }

    @Test
    void testEmailGetterAndSetter() {
        String newEmail = "new-email@example.com";
        score.setEmail(newEmail);
        assertEquals(newEmail, score.getEmail());
    }

    @Test
    void testPlayTimeGetterAndSetter() {
        int newPlayTime = 600;
        score.setPlayTime(newPlayTime);
        assertEquals(newPlayTime, score.getPlayTime());
    }
}
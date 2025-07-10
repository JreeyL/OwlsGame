package org.OwlsGame.backend.dao;

import org.OwlsGame.backend.models.Score;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ScoreRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ScoreRepository scoreRepository;

    private Score createTestScore(int userId, int gameId, int scoreValue, String email, int playTime) {
        Timestamp now = Timestamp.from(Instant.now());
        return new Score(userId, gameId, scoreValue, now, email, playTime);
    }

    @Test
    public void whenFindByUserId_thenReturnScores() {
        int userId = 1;
        Score score1 = createTestScore(userId, 1, 100, "user1@example.com", 60);
        Score score2 = createTestScore(userId, 2, 200, "user1@example.com", 120);
        Score score3 = createTestScore(2, 1, 150, "user2@example.com", 90);

        entityManager.persist(score1);
        entityManager.persist(score2);
        entityManager.persist(score3);
        entityManager.flush();

        List<Score> userScores = scoreRepository.findByUserId(userId);

        assertEquals(2, userScores.size());
        assertTrue(userScores.stream().allMatch(s -> s.getUserId() == userId));
    }

    @Test
    public void whenFindByGameId_thenReturnScores() {
        int gameId = 1;
        Score score1 = createTestScore(1, gameId, 100, "user1@example.com", 60);
        Score score2 = createTestScore(2, gameId, 200, "user2@example.com", 120);
        Score score3 = createTestScore(1, 2, 150, "user1@example.com", 90);

        entityManager.persist(score1);
        entityManager.persist(score2);
        entityManager.persist(score3);
        entityManager.flush();

        List<Score> gameScores = scoreRepository.findByGameId(gameId);

        assertEquals(2, gameScores.size());
        assertTrue(gameScores.stream().allMatch(s -> s.getGameId() == gameId));
    }

    @Test
    public void whenFindByNonExistingUserId_thenReturnEmptyList() {
        int nonExistingUserId = 999;
        List<Score> userScores = scoreRepository.findByUserId(nonExistingUserId);
        assertTrue(userScores.isEmpty());
    }

    @Test
    public void whenFindByNonExistingGameId_thenReturnEmptyList() {
        int nonExistingGameId = 999;
        List<Score> gameScores = scoreRepository.findByGameId(nonExistingGameId);
        assertTrue(gameScores.isEmpty());
    }

    @Test
    public void whenSaveScore_thenScoreIsPersisted() {
        Score score = createTestScore(1, 1, 100, "user1@example.com", 60);
        Score savedScore = scoreRepository.save(score);
        assertNotNull(savedScore.getId());
        assertEquals(score.getUserId(), savedScore.getUserId());
        assertEquals(score.getGameId(), savedScore.getGameId());
        assertEquals(score.getScoreValue(), savedScore.getScoreValue());
        assertEquals(score.getEmail(), savedScore.getEmail());
        assertEquals(score.getPlayTime(), savedScore.getPlayTime());
        assertNotNull(savedScore.getTimestamp());
    }

    @Test
    public void whenFindById_thenReturnScore() {
        Score score = createTestScore(1, 1, 100, "user1@example.com", 60);
        score = entityManager.persist(score);
        entityManager.flush();
        int scoreId = score.getId();

        Optional<Score> found = scoreRepository.findById(scoreId);

        assertTrue(found.isPresent());
        assertEquals(scoreId, found.get().getId());
    }

    @Test
    public void whenFindByNonExistingId_thenReturnEmpty() {
        int nonExistingId = 9999;
        Optional<Score> found = scoreRepository.findById(nonExistingId);
        assertFalse(found.isPresent());
    }

    @Test
    public void whenUpdateScore_thenScoreIsUpdated() {
        Score score = createTestScore(1, 1, 100, "user1@example.com", 60);
        score = entityManager.persist(score);
        entityManager.flush();

        score.setScoreValue(150);
        score.setPlayTime(90);
        Score updatedScore = scoreRepository.save(score);
        entityManager.flush();

        Score retrievedScore = entityManager.find(Score.class, score.getId());
        assertEquals(150, retrievedScore.getScoreValue());
        assertEquals(90, retrievedScore.getPlayTime());
    }

    @Test
    public void whenDeleteScore_thenScoreIsRemoved() {
        Score score = createTestScore(1, 1, 100, "user1@example.com", 60);
        score = entityManager.persist(score);
        entityManager.flush();
        int scoreId = score.getId();

        scoreRepository.deleteById(scoreId);
        entityManager.flush();

        Score deletedScore = entityManager.find(Score.class, scoreId);
        assertNull(deletedScore);
    }

    @Test
    public void whenFindAll_thenReturnAllScores() {
        Score score1 = createTestScore(1, 1, 100, "user1@example.com", 60);
        Score score2 = createTestScore(2, 1, 200, "user2@example.com", 120);
        Score score3 = createTestScore(1, 2, 150, "user1@example.com", 90);

        entityManager.persist(score1);
        entityManager.persist(score2);
        entityManager.persist(score3);
        entityManager.flush();

        List<Score> scores = scoreRepository.findAll();

        assertEquals(3, scores.size());
    }

    @Test
    public void whenFindTopByUserIdAndGameIdOrderByScoreValueDesc_thenReturnHighestScore() {
        int userId = 1, gameId = 1;
        Score lowScore = createTestScore(userId, gameId, 100, "user1@example.com", 60);
        Score highScore = createTestScore(userId, gameId, 200, "user1@example.com", 60);

        entityManager.persist(lowScore);
        entityManager.persist(highScore);
        entityManager.flush();

        Optional<Score> found = scoreRepository.findTopByUserIdAndGameIdOrderByScoreValueDesc(userId, gameId);
        assertTrue(found.isPresent());
        assertEquals(200, found.get().getScoreValue());
    }

    @Test
    public void whenFindTopScoresByGameId_thenReturnTopNOrdered() {
        int gameId = 1;
        Score s1 = createTestScore(1, gameId, 100, "a@example.com", 60);
        Score s2 = createTestScore(2, gameId, 300, "b@example.com", 60);
        Score s3 = createTestScore(3, gameId, 200, "c@example.com", 60);

        entityManager.persist(s1);
        entityManager.persist(s2);
        entityManager.persist(s3);
        entityManager.flush();

        List<Score> top2 = scoreRepository.findTopScoresByGameId(gameId, PageRequest.of(0, 2));
        assertEquals(2, top2.size());
        assertEquals(300, top2.get(0).getScoreValue());
        assertEquals(200, top2.get(1).getScoreValue());
    }

    @Test
    public void whenGetTotalPlayTimeByUserIdAndGameId_thenReturnSum() {
        int userId = 1, gameId = 1;
        Score s1 = createTestScore(userId, gameId, 100, "a@example.com", 10);
        Score s2 = createTestScore(userId, gameId, 200, "a@example.com", 20);

        entityManager.persist(s1);
        entityManager.persist(s2);
        entityManager.flush();

        Integer total = scoreRepository.getTotalPlayTimeByUserIdAndGameId(userId, gameId);
        assertEquals(30, total);
    }

    @Test
    public void whenGetTotalPlayTimeGroupByGameId_thenReturnList() {
        int userId = 1;
        Score s1 = createTestScore(userId, 1, 100, "a@example.com", 10);
        Score s2 = createTestScore(userId, 1, 200, "a@example.com", 20);
        Score s3 = createTestScore(userId, 2, 150, "a@example.com", 30);

        entityManager.persist(s1);
        entityManager.persist(s2);
        entityManager.persist(s3);
        entityManager.flush();

        List<Object[]> result = scoreRepository.getTotalPlayTimeGroupByGameId(userId);

        assertEquals(2, result.size());
        // result: [gameId, totalPlayTime]
        for (Object[] entry : result) {
            Integer gameId = (Integer) entry[0];
            Long totalPlayTime = (Long) entry[1];
            if (gameId == 1) {
                assertEquals(30L, totalPlayTime);
            } else if (gameId == 2) {
                assertEquals(30L, totalPlayTime);
            }
        }
    }

    @Test
    public void whenSaveScoreWithMissingRequiredFields_thenShouldFail() {
        Score score = new Score();
        assertThrows(Exception.class, () -> {
            entityManager.persist(score);
            entityManager.flush();
        });
    }
}
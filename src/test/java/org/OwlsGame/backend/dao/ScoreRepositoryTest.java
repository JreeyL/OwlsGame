package org.OwlsGame.backend.dao;

import org.OwlsGame.backend.models.Score;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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

    // 创建测试分数记录的辅助方法
    private Score createTestScore(int userId, int gameId, int scoreValue, String email, int playTime) {
        Timestamp now = Timestamp.from(Instant.now());
        return new Score(userId, gameId, scoreValue, now, email, playTime);
    }

    @Test
    public void whenFindByUserId_thenReturnScores() {
        // given
        int userId = 1;
        Score score1 = createTestScore(userId, 1, 100, "user1@example.com", 60);
        Score score2 = createTestScore(userId, 2, 200, "user1@example.com", 120);
        Score score3 = createTestScore(2, 1, 150, "user2@example.com", 90); // 不同用户

        entityManager.persist(score1);
        entityManager.persist(score2);
        entityManager.persist(score3);
        entityManager.flush();

        // when
        List<Score> userScores = scoreRepository.findByUserId(userId);

        // then
        assertEquals(2, userScores.size());
        assertTrue(userScores.stream().allMatch(s -> s.getUserId() == userId));
    }

    @Test
    public void whenFindByNonExistingUserId_thenReturnEmptyList() {
        // given
        int nonExistingUserId = 999;

        // when
        List<Score> userScores = scoreRepository.findByUserId(nonExistingUserId);

        // then
        assertTrue(userScores.isEmpty());
    }

    @Test
    public void whenFindByGameId_thenReturnScores() {
        // given
        int gameId = 1;
        Score score1 = createTestScore(1, gameId, 100, "user1@example.com", 60);
        Score score2 = createTestScore(2, gameId, 200, "user2@example.com", 120);
        Score score3 = createTestScore(1, 2, 150, "user1@example.com", 90); // 不同游戏

        entityManager.persist(score1);
        entityManager.persist(score2);
        entityManager.persist(score3);
        entityManager.flush();

        // when
        List<Score> gameScores = scoreRepository.findByGameId(gameId);

        // then
        assertEquals(2, gameScores.size());
        assertTrue(gameScores.stream().allMatch(s -> s.getGameId() == gameId));
    }

    @Test
    public void whenFindByNonExistingGameId_thenReturnEmptyList() {
        // given
        int nonExistingGameId = 999;

        // when
        List<Score> gameScores = scoreRepository.findByGameId(nonExistingGameId);

        // then
        assertTrue(gameScores.isEmpty());
    }

    @Test
    public void whenSaveScore_thenScoreIsPersisted() {
        // given
        Score score = createTestScore(1, 1, 100, "user1@example.com", 60);

        // when
        Score savedScore = scoreRepository.save(score);

        // then
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
        // given
        Score score = createTestScore(1, 1, 100, "user1@example.com", 60);
        score = entityManager.persist(score);
        entityManager.flush();
        int scoreId = score.getId();

        // when
        Optional<Score> found = scoreRepository.findById(scoreId);

        // then
        assertTrue(found.isPresent());
        assertEquals(scoreId, found.get().getId());
        assertEquals(score.getUserId(), found.get().getUserId());
        assertEquals(score.getScoreValue(), found.get().getScoreValue());
    }

    @Test
    public void whenFindByNonExistingId_thenReturnEmpty() {
        // given
        int nonExistingId = 9999;

        // when
        Optional<Score> found = scoreRepository.findById(nonExistingId);

        // then
        assertFalse(found.isPresent());
    }

    @Test
    public void whenUpdateScore_thenScoreIsUpdated() {
        // given
        Score score = createTestScore(1, 1, 100, "user1@example.com", 60);
        score = entityManager.persist(score);
        entityManager.flush();

        // when
        score.setScoreValue(150);
        score.setPlayTime(90);
        Score updatedScore = scoreRepository.save(score);
        entityManager.flush();

        // then
        Score retrievedScore = entityManager.find(Score.class, score.getId());
        assertEquals(150, retrievedScore.getScoreValue());
        assertEquals(90, retrievedScore.getPlayTime());
    }

    @Test
    public void whenDeleteScore_thenScoreIsRemoved() {
        // given
        Score score = createTestScore(1, 1, 100, "user1@example.com", 60);
        score = entityManager.persist(score);
        entityManager.flush();
        int scoreId = score.getId();

        // when
        scoreRepository.deleteById(scoreId);
        entityManager.flush();

        // then
        Score deletedScore = entityManager.find(Score.class, scoreId);
        assertNull(deletedScore);
    }

    @Test
    public void whenFindAll_thenReturnAllScores() {
        // given
        Score score1 = createTestScore(1, 1, 100, "user1@example.com", 60);
        Score score2 = createTestScore(2, 1, 200, "user2@example.com", 120);
        Score score3 = createTestScore(1, 2, 150, "user1@example.com", 90);

        entityManager.persist(score1);
        entityManager.persist(score2);
        entityManager.persist(score3);
        entityManager.flush();

        // when
        List<Score> scores = scoreRepository.findAll();

        // then
        assertEquals(3, scores.size());
    }

    @Test
    public void whenFindByUserIdAndGameId_thenReturnMatchingScores() {
        // given
        int userId = 1;
        int gameId = 2;

        Score score1 = createTestScore(userId, gameId, 100, "user1@example.com", 60); // 匹配
        Score score2 = createTestScore(userId, 1, 200, "user1@example.com", 120);     // 只匹配userId
        Score score3 = createTestScore(2, gameId, 150, "user2@example.com", 90);      // 只匹配gameId
        Score score4 = createTestScore(2, 1, 300, "user2@example.com", 180);          // 都不匹配

        entityManager.persist(score1);
        entityManager.persist(score2);
        entityManager.persist(score3);
        entityManager.persist(score4);
        entityManager.flush();

        // when - 使用单独的查询，然后结合结果
        List<Score> userScores = scoreRepository.findByUserId(userId);
        List<Score> gameScores = scoreRepository.findByGameId(gameId);

        // then - 验证筛选结果
        List<Score> matchingUserScores = userScores.stream()
                .filter(s -> s.getGameId() == gameId)
                .toList();

        List<Score> matchingGameScores = gameScores.stream()
                .filter(s -> s.getUserId() == userId)
                .toList();

        assertEquals(1, matchingUserScores.size());
        assertEquals(1, matchingGameScores.size());
        assertEquals(score1.getId(), matchingUserScores.get(0).getId());
        assertEquals(score1.getId(), matchingGameScores.get(0).getId());
    }

    @Test
    public void whenSaveScoreWithMissingRequiredFields_thenShouldFail() {
        // given
        Score score = new Score();
        // 没有设置任何必需字段

        // then
        assertThrows(Exception.class, () -> {
            // when
            entityManager.persist(score);
            entityManager.flush();
        });
    }
}
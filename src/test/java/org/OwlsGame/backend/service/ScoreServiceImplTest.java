package org.OwlsGame.backend.service;

import org.OwlsGame.backend.dao.ScoreRepository;
import org.OwlsGame.backend.models.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScoreServiceImplTest {

    @Mock
    private ScoreRepository scoreRepository;

    @InjectMocks
    private ScoreServiceImpl scoreService;

    private Score testScore;
    private Score anotherScore;
    private final Integer scoreId = 1;
    private final Integer userId = 101;
    private final Integer gameId = 201;

    @BeforeEach
    void setUp() {
        // 创建测试用的分数对象
        Timestamp now = Timestamp.from(Instant.now());

        testScore = new Score(userId, gameId, 100, now, "user@example.com", 60);
        testScore.setId(scoreId);

        anotherScore = new Score(userId, 202, 200, now, "user@example.com", 120);
        anotherScore.setId(2);
    }

    @Test
    void whenCreateScore_thenScoreRepositorySaveIsCalled() {
        // given
        when(scoreRepository.save(any(Score.class))).thenReturn(testScore);

        // when
        scoreService.createScore(testScore);

        // then
        verify(scoreRepository, times(1)).save(testScore);
    }

    @Test
    void whenGetScoreById_thenReturnScore() {
        // given
        when(scoreRepository.findById(scoreId)).thenReturn(Optional.of(testScore));

        // when
        Score foundScore = scoreService.getScoreById(scoreId);

        // then
        assertNotNull(foundScore);
        assertEquals(scoreId, foundScore.getId());
        assertEquals(userId, foundScore.getUserId());
        assertEquals(gameId, foundScore.getGameId());
        verify(scoreRepository, times(1)).findById(scoreId);
    }

    @Test
    void whenGetScoreByNonExistingId_thenReturnNull() {
        // given
        Integer nonExistingId = 999;
        when(scoreRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // when
        Score foundScore = scoreService.getScoreById(nonExistingId);

        // then
        assertNull(foundScore);
        verify(scoreRepository, times(1)).findById(nonExistingId);
    }

    @Test
    void whenGetAllScores_thenReturnList() {
        // given
        List<Score> scores = Arrays.asList(testScore, anotherScore);
        when(scoreRepository.findAll()).thenReturn(scores);

        // when
        List<Score> allScores = scoreService.getAllScores();

        // then
        assertEquals(2, allScores.size());
        verify(scoreRepository, times(1)).findAll();
    }

    @Test
    void whenUpdateScore_thenScoreRepositorySaveIsCalled() {
        // given
        testScore.setScoreValue(150);
        when(scoreRepository.save(any(Score.class))).thenReturn(testScore);

        // when
        scoreService.updateScore(testScore);

        // then
        verify(scoreRepository, times(1)).save(testScore);
    }

    @Test
    void whenDeleteScoreById_thenScoreRepositoryDeleteByIdIsCalled() {
        // given
        doNothing().when(scoreRepository).deleteById(scoreId);

        // when
        scoreService.deleteScoreById(scoreId);

        // then
        verify(scoreRepository, times(1)).deleteById(scoreId);
    }

    @Test
    void whenGetScoresByUserId_thenReturnScoresList() {
        // given
        List<Score> userScores = Arrays.asList(testScore, anotherScore);
        when(scoreRepository.findByUserId(userId)).thenReturn(userScores);

        // when
        List<Score> foundScores = scoreService.getScoresByUserId(userId);

        // then
        assertEquals(2, foundScores.size());
        verify(scoreRepository, times(1)).findByUserId(userId);
    }

    @Test
    void whenGetScoresByNonExistingUserId_thenReturnEmptyList() {
        // given
        Integer nonExistingUserId = 999;
        when(scoreRepository.findByUserId(nonExistingUserId)).thenReturn(Collections.emptyList());

        // when
        List<Score> foundScores = scoreService.getScoresByUserId(nonExistingUserId);

        // then
        assertTrue(foundScores.isEmpty());
        verify(scoreRepository, times(1)).findByUserId(nonExistingUserId);
    }

    @Test
    void whenGetScoresByGameId_thenReturnScoresList() {
        // given
        List<Score> gameScores = List.of(testScore);
        when(scoreRepository.findByGameId(gameId)).thenReturn(gameScores);

        // when
        List<Score> foundScores = scoreService.getScoresByGameId(gameId);

        // then
        assertEquals(1, foundScores.size());
        assertEquals(gameId, foundScores.get(0).getGameId());
        verify(scoreRepository, times(1)).findByGameId(gameId);
    }

    @Test
    void whenGetScoresByNonExistingGameId_thenReturnEmptyList() {
        // given
        Integer nonExistingGameId = 999;
        when(scoreRepository.findByGameId(nonExistingGameId)).thenReturn(Collections.emptyList());

        // when
        List<Score> foundScores = scoreService.getScoresByGameId(nonExistingGameId);

        // then
        assertTrue(foundScores.isEmpty());
        verify(scoreRepository, times(1)).findByGameId(nonExistingGameId);
    }

    @Test
    void whenSaveScore_thenScoreRepositorySaveIsCalled() {
        // given
        when(scoreRepository.save(any(Score.class))).thenReturn(testScore);

        // when
        scoreService.saveScore(testScore);

        // then
        verify(scoreRepository, times(1)).save(testScore);
    }

    @Test
    void whenSaveScoreWithNullId_thenScoreRepositorySaveIsCalled() {
        // given
        Score newScore = new Score(userId, gameId, 150, Timestamp.from(Instant.now()), "user@example.com", 90);
        // Id is not set (null)
        when(scoreRepository.save(any(Score.class))).thenReturn(newScore);

        // when
        scoreService.saveScore(newScore);

        // then
        verify(scoreRepository, times(1)).save(newScore);
    }

    @Test
    void whenGetAllScoresReturnsEmpty_thenReturnEmptyList() {
        // given
        when(scoreRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<Score> allScores = scoreService.getAllScores();

        // then
        assertTrue(allScores.isEmpty());
        verify(scoreRepository, times(1)).findAll();
    }
}
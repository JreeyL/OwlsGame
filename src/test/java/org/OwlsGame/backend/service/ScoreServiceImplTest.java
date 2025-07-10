package org.OwlsGame.backend.service;

import org.OwlsGame.backend.dao.ScoreRepository;
import org.OwlsGame.backend.models.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

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
        Timestamp now = Timestamp.from(Instant.now());

        testScore = new Score(userId, gameId, 100, now, "user@example.com", 60);
        testScore.setId(scoreId);

        anotherScore = new Score(userId, 202, 200, now, "user@example.com", 120);
        anotherScore.setId(2);
    }

    @Test
    void whenSaveScore_thenRepositorySaveIsCalled() {
        when(scoreRepository.save(any(Score.class))).thenReturn(testScore);

        Score result = scoreService.saveScore(testScore);

        verify(scoreRepository, times(1)).save(testScore);
        assertEquals(testScore, result);
    }

    @Test
    void whenGetScoreById_thenReturnScore() {
        when(scoreRepository.findById(scoreId)).thenReturn(Optional.of(testScore));

        Score foundScore = scoreService.getScoreById(scoreId);

        assertNotNull(foundScore);
        assertEquals(scoreId, foundScore.getId());
        assertEquals(userId, foundScore.getUserId());
        assertEquals(gameId, foundScore.getGameId());
        verify(scoreRepository, times(1)).findById(scoreId);
    }

    @Test
    void whenGetScoreByNonExistingId_thenReturnNull() {
        Integer nonExistingId = 999;
        when(scoreRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Score foundScore = scoreService.getScoreById(nonExistingId);

        assertNull(foundScore);
        verify(scoreRepository, times(1)).findById(nonExistingId);
    }

    @Test
    void whenGetAllScores_thenReturnList() {
        List<Score> scores = Arrays.asList(testScore, anotherScore);
        when(scoreRepository.findAll()).thenReturn(scores);

        List<Score> allScores = scoreService.getAllScores();

        assertEquals(2, allScores.size());
        verify(scoreRepository, times(1)).findAll();
    }

    @Test
    void whenGetAllScoresReturnsEmpty_thenReturnEmptyList() {
        when(scoreRepository.findAll()).thenReturn(Collections.emptyList());

        List<Score> allScores = scoreService.getAllScores();

        assertTrue(allScores.isEmpty());
        verify(scoreRepository, times(1)).findAll();
    }

    @Test
    void whenDeleteScoreById_thenRepositoryDeleteByIdIsCalled() {
        doNothing().when(scoreRepository).deleteById(scoreId);

        scoreService.deleteScoreById(scoreId);

        verify(scoreRepository, times(1)).deleteById(scoreId);
    }

    @Test
    void whenGetScoresByUserId_thenReturnList() {
        List<Score> userScores = Arrays.asList(testScore, anotherScore);
        when(scoreRepository.findByUserId(userId)).thenReturn(userScores);

        List<Score> foundScores = scoreService.getScoresByUserId(userId);

        assertEquals(2, foundScores.size());
        verify(scoreRepository, times(1)).findByUserId(userId);
    }

    @Test
    void whenGetScoresByGameId_thenReturnList() {
        List<Score> gameScores = List.of(testScore);
        when(scoreRepository.findByGameId(gameId)).thenReturn(gameScores);

        List<Score> foundScores = scoreService.getScoresByGameId(gameId);

        assertEquals(1, foundScores.size());
        assertEquals(gameId, foundScores.get(0).getGameId());
        verify(scoreRepository, times(1)).findByGameId(gameId);
    }

    @Test
    void whenGetHighestScoreByUserAndGame_thenReturnOptionalScore() {
        when(scoreRepository.findTopByUserIdAndGameIdOrderByScoreValueDesc(userId, gameId))
                .thenReturn(Optional.of(testScore));

        Optional<Score> result = scoreService.getHighestScoreByUserAndGame(userId, gameId);

        assertTrue(result.isPresent());
        assertEquals(testScore, result.get());
        verify(scoreRepository, times(1))
                .findTopByUserIdAndGameIdOrderByScoreValueDesc(userId, gameId);
    }

    @Test
    void whenGetTopNScoresByGame_thenReturnList() {
        int n = 2;
        List<Score> topScores = Arrays.asList(testScore, anotherScore);
        when(scoreRepository.findTopScoresByGameId(eq(gameId), any(PageRequest.class)))
                .thenReturn(topScores);

        List<Score> result = scoreService.getTopNScoresByGame(gameId, n);

        assertEquals(2, result.size());
        verify(scoreRepository, times(1))
                .findTopScoresByGameId(eq(gameId), any(PageRequest.class));
    }

    @Test
    void whenGetTotalPlayTimeByUserAndGame_thenReturnValue() {
        Integer totalPlayTime = 180;
        when(scoreRepository.getTotalPlayTimeByUserIdAndGameId(userId, gameId))
                .thenReturn(totalPlayTime);

        Integer result = scoreService.getTotalPlayTimeByUserAndGame(userId, gameId);

        assertEquals(totalPlayTime, result);
        verify(scoreRepository, times(1))
                .getTotalPlayTimeByUserIdAndGameId(userId, gameId);
    }

    @Test
    void whenGetTotalPlayTimeGroupByGame_thenReturnList() {
        List<Object[]> playTimes = Arrays.asList(
                new Object[]{gameId, 100},
                new Object[]{202, 200}
        );
        when(scoreRepository.getTotalPlayTimeGroupByGameId(userId)).thenReturn(playTimes);

        List<Object[]> result = scoreService.getTotalPlayTimeGroupByGame(userId);

        assertEquals(2, result.size());
        verify(scoreRepository, times(1)).getTotalPlayTimeGroupByGameId(userId);
    }
}
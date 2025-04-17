package org.OwlsGame.backend.service;

import org.OwlsGame.backend.models.Score;
import java.util.List;
import java.util.Optional;

public interface ScoreService {
    void createScore(Score score);
    Score getScoreById(Integer id);
    List<Score> getAllScores();
    void updateScore(Score score);
    void deleteScoreById(Integer id);
    List<Score> getScoresByUserId(Integer userId);
    List<Score> getScoresByGameId(Integer gameId);

    Optional<Score> getHighestScoreByUserAndGame(Integer userId, Integer gameId);
    List<Score> getTopNScoresByGame(Integer gameId, int n);
    Integer getTotalPlayTimeByUserAndGame(Integer userId, Integer gameId);
    List<Object[]> getTotalPlayTimeGroupByGame(Integer userId);

    void saveScore(Score score);
}
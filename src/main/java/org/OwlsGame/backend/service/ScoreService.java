package org.OwlsGame.backend.service;

import java.util.List;
import java.util.Optional;

import org.OwlsGame.backend.models.Score;

public interface ScoreService {
    Score saveScore(Score score);
    Score getScoreById(Integer id);
    List<Score> getAllScores();
    void deleteScoreById(Integer id);
    List<Score> getScoresByUserId(Integer userId);
    List<Score> getScoresByGameId(Integer gameId);

    Optional<Score> getHighestScoreByUserAndGame(Integer userId, Integer gameId);
    List<Score> getTopNScoresByGame(Integer gameId, int n);
    List<Score> getTopScoresByGame(String gameName, int n);
    Integer getTotalPlayTimeByUserAndGame(Integer userId, Integer gameId);
    List<Object[]> getTotalPlayTimeGroupByGame(Integer userId);
}
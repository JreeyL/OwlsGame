package org.OwlsGame.backend.service;

import org.OwlsGame.backend.models.Score;
import java.util.List;

public interface ScoreService {
    void createScore(Score score);
    Score getScoreById(int id);
    List<Score> getAllScores();
    void updateScore(Score score);
    void deleteScoreById(int id);
    List<Score> getScoresByUserId(int userId);
    List<Score> getScoresByGameId(int gameId);

    // New method to save score
    void saveScore(Score score);
}
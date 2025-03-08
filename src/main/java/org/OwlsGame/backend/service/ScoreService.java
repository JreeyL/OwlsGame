package org.OwlsGame.backend.service;

import org.OwlsGame.backend.models.Score;
import java.util.List;

public interface ScoreService {
    void createScore(Score score);
    Score getScoreById(Integer id); // 修改为Integer
    List<Score> getAllScores();
    void updateScore(Score score);
    void deleteScoreById(Integer id); // 修改为Integer
    List<Score> getScoresByUserId(Integer userId); // 修改为Integer
    List<Score> getScoresByGameId(Integer gameId); // 修改为Integer
    void saveScore(Score score);

}
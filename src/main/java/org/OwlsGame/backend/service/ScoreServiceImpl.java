package org.OwlsGame.backend.service;


import org.OwlsGame.backend.models.Score;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreDAO scoreDAO;

    @Override
    public void createScore(Score score) {
        try {
            scoreDAO.addScore(score);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Score getScoreById(int id) {
        Score score = null;
        try {
            score = scoreDAO.getScoreById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return score;
    }

    @Override
    public List<Score> getAllScores() {
        List<Score> scores = null;
        try {
            scores = scoreDAO.getAllScores();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    @Override
    public void updateScore(Score score) {
        try {
            scoreDAO.updateScore(score);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteScoreById(int id) {
        try {
            scoreDAO.deleteScoreById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Score> getScoresByUserId(int userId) {
        List<Score> scores = null;
        try {
            scores = scoreDAO.getScoresByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    @Override
    public List<Score> getScoresByGameId(int gameId) {
        List<Score> scores = null;
        try {
            scores = scoreDAO.getScoresByGameId(gameId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    @Override
    public void saveScore(Score score) {
        try {
            scoreDAO.addScore(score);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

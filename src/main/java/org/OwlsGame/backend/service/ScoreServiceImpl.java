package org.OwlsGame.backend.service;

import org.OwlsGame.backend.dao.ScoreRepository;
import org.OwlsGame.backend.models.Score;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScoreServiceImpl implements ScoreService {

    private final ScoreRepository scoreRepository;

    @Autowired
    public ScoreServiceImpl(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    @Override
    public void createScore(Score score) {
        scoreRepository.save(score);
    }

    @Override
    public Score getScoreById(Integer id) {
        return scoreRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Score> getAllScores() {
        return scoreRepository.findAll();
    }

    @Override
    public void updateScore(Score score) {
        scoreRepository.save(score);
    }

    @Override
    public void deleteScoreById(Integer id) {
        scoreRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Score> getScoresByUserId(Integer userId) {
        return scoreRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Score> getScoresByGameId(Integer gameId) {
        return scoreRepository.findByGameId(gameId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Score> getHighestScoreByUserAndGame(Integer userId, Integer gameId) {
        return scoreRepository.findTopByUserIdAndGameIdOrderByScoreValueDesc(userId, gameId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Score> getTopNScoresByGame(Integer gameId, int n) {
        return scoreRepository.findTopScoresByGameId(gameId, PageRequest.of(0, n));
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalPlayTimeByUserAndGame(Integer userId, Integer gameId) {
        return scoreRepository.getTotalPlayTimeByUserIdAndGameId(userId, gameId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTotalPlayTimeGroupByGame(Integer userId) {
        return scoreRepository.getTotalPlayTimeGroupByGameId(userId);
    }

    @Override
    public void saveScore(Score score) {
        scoreRepository.save(score);
    }
}
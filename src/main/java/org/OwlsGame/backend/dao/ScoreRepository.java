package org.OwlsGame.backend.dao;

import org.OwlsGame.backend.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Integer> {

    // 根据用户ID查询分数记录（替代getScoresByUserId）
    List<Score> findByUserId(int userId);

    // 根据游戏ID查询分数记录（替代getScoresByGameId）
    List<Score> findByGameId(int gameId);
}
package org.OwlsGame.backend.dao;

import java.util.List;
import java.util.Optional;

import org.OwlsGame.backend.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScoreRepository extends JpaRepository<Score, Integer> {

    List<Score> findByUserId(int userId);

    List<Score> findByGameId(int gameId);

    // 历史最高分（单用户单游戏）
    Optional<Score> findTopByUserIdAndGameIdOrderByScoreValueDesc(int userId, int gameId);

    // 全部玩家某游戏的最高分排行榜（前N名，按分数降序时间升序）
    @Query("SELECT s FROM Score s WHERE s.gameId = :gameId ORDER BY s.scoreValue DESC, s.timestamp ASC")
    List<Score> findTopScoresByGameId(int gameId, org.springframework.data.domain.Pageable pageable);

    // 按游戏名称查询排行榜（前N名，按分数降序时间升序）
    @Query("SELECT s FROM Score s, Game g WHERE s.gameId = g.id AND g.name = :gameName ORDER BY s.scoreValue DESC, s.timestamp ASC")
    List<Score> findTopScoresByGameName(String gameName, org.springframework.data.domain.Pageable pageable);

    // 某玩家玩某游戏的总时长
    @Query("SELECT SUM(s.playTime) FROM Score s WHERE s.userId = :userId AND s.gameId = :gameId")
    Integer getTotalPlayTimeByUserIdAndGameId(int userId, int gameId);

    // 某玩家所有游戏的总时长
    @Query("SELECT s.gameId, SUM(s.playTime) FROM Score s WHERE s.userId = :userId GROUP BY s.gameId")
    List<Object[]> getTotalPlayTimeGroupByGameId(int userId);
}
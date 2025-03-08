package org.OwlsGame.backend.dao;

import org.OwlsGame.backend.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    // 根据游戏名称查找游戏
    Optional<Game> findByName(String name);

    // 根据最高分查找游戏
    List<Game> findByMaxScore(int maxScore);
}
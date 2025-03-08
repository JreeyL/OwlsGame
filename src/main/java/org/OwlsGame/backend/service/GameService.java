package org.OwlsGame.backend.service;

import org.OwlsGame.backend.models.Game;
import java.util.List;
import java.util.Optional;

public interface GameService {
    // 基础CRUD操作
    Game createGame(Game game);
    Optional<Game> getGameById(Long id); // 改为Long类型
    List<Game> getAllGames();
    Game updateGame(Game game);
    void deleteGame(Long id);

    // 扩展查询方法
    Optional<Game> findByName(String name);
    List<Game> findByMaxScore(int maxScore);

    // 业务方法
    void resetGame(Long gameId);
}
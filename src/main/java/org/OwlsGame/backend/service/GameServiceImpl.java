package org.OwlsGame.backend.service;

import org.OwlsGame.backend.dao.GameRepository;
import org.OwlsGame.backend.models.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Game createGame(Game game) {
        return gameRepository.save(game);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public Game updateGame(Game game) {
        // 确保游戏存在
        if (gameRepository.existsById(game.getId())) {
            return gameRepository.save(game);
        }
        throw new IllegalArgumentException("Game not found with id: " + game.getId());
    }

    @Override
    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Game> findByName(String name) {
        return gameRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Game> findByMaxScore(int maxScore) {
        return gameRepository.findByMaxScore(maxScore);
    }

    @Override
    public void resetGame(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        game.resetGame(); // 调用实体类方法
        gameRepository.save(game);
    }
}
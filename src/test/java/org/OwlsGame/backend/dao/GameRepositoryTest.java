package org.OwlsGame.backend.dao;

import org.OwlsGame.backend.models.Game;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GameRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    // 创建测试游戏的辅助方法
    private Game createTestGame(String name, int maxScore) {
        return new Game(name, maxScore);
    }

    @Test
    public void whenFindByName_thenReturnGame() {
        // given
        Game game = createTestGame("测试游戏", 100);
        entityManager.persist(game);
        entityManager.flush();

        // when
        Optional<Game> found = gameRepository.findByName(game.getName());

        // then
        assertTrue(found.isPresent());
        assertEquals(game.getName(), found.get().getName());
        assertEquals(game.getMaxScore(), found.get().getMaxScore());
    }

    @Test
    public void whenFindByNonExistingName_thenReturnEmpty() {
        // given
        String nonExistingName = "不存在的游戏";

        // when
        Optional<Game> found = gameRepository.findByName(nonExistingName);

        // then
        assertFalse(found.isPresent());
    }

    @Test
    public void whenFindByMaxScore_thenReturnGames() {
        // given
        int targetMaxScore = 200;
        Game game1 = createTestGame("游戏1", targetMaxScore);
        Game game2 = createTestGame("游戏2", targetMaxScore);
        Game game3 = createTestGame("游戏3", 300); // 不同分数的游戏

        entityManager.persist(game1);
        entityManager.persist(game2);
        entityManager.persist(game3);
        entityManager.flush();

        // when
        List<Game> found = gameRepository.findByMaxScore(targetMaxScore);

        // then
        assertEquals(2, found.size());
        assertTrue(found.stream().allMatch(g -> g.getMaxScore() == targetMaxScore));
    }

    @Test
    public void whenFindByNonExistingMaxScore_thenReturnEmptyList() {
        // given
        int nonExistingMaxScore = 999;

        // when
        List<Game> found = gameRepository.findByMaxScore(nonExistingMaxScore);

        // then
        assertTrue(found.isEmpty());
    }

    @Test
    public void whenSaveGame_thenGameIsPersisted() {
        // given
        Game game = createTestGame("新游戏", 150);

        // when
        Game savedGame = gameRepository.save(game);

        // then
        assertNotNull(savedGame.getId());
        assertEquals(game.getName(), savedGame.getName());
        assertEquals(game.getMaxScore(), savedGame.getMaxScore());
    }

    @Test
    public void whenFindById_thenReturnGame() {
        // given
        Game game = createTestGame("ID查询测试", 250);
        game = entityManager.persist(game);
        entityManager.flush();
        Long gameId = game.getId();

        // when
        Optional<Game> found = gameRepository.findById(gameId);

        // then
        assertTrue(found.isPresent());
        assertEquals(gameId, found.get().getId());
        assertEquals(game.getName(), found.get().getName());
    }

    @Test
    public void whenFindByNonExistingId_thenReturnEmpty() {
        // given
        Long nonExistingId = 9999L;

        // when
        Optional<Game> found = gameRepository.findById(nonExistingId);

        // then
        assertFalse(found.isPresent());
    }

    @Test
    public void whenUpdateGame_thenGameIsUpdated() {
        // given
        Game game = createTestGame("原始游戏", 300);
        game = entityManager.persist(game);
        entityManager.flush();

        // when
        game.setName("更新后的游戏");
        game.setMaxScore(400);
        Game updatedGame = gameRepository.save(game);
        entityManager.flush();

        // then
        Game retrievedGame = entityManager.find(Game.class, game.getId());
        assertEquals("更新后的游戏", retrievedGame.getName());
        assertEquals(400, retrievedGame.getMaxScore());
    }

    @Test
    public void whenDeleteGame_thenGameIsRemoved() {
        // given
        Game game = createTestGame("待删除游戏", 100);
        game = entityManager.persist(game);
        entityManager.flush();
        Long gameId = game.getId();

        // when
        gameRepository.deleteById(gameId);
        entityManager.flush();

        // then
        Game deletedGame = entityManager.find(Game.class, gameId);
        assertNull(deletedGame);
    }

    @Test
    public void whenFindAll_thenReturnAllGames() {
        // given
        Game game1 = createTestGame("全部游戏1", 100);
        Game game2 = createTestGame("全部游戏2", 200);
        Game game3 = createTestGame("全部游戏3", 300);

        entityManager.persist(game1);
        entityManager.persist(game2);
        entityManager.persist(game3);
        entityManager.flush();

        // when
        List<Game> games = gameRepository.findAll();

        // then
        assertEquals(3, games.size());
        assertTrue(games.stream().anyMatch(g -> g.getName().equals("全部游戏1")));
        assertTrue(games.stream().anyMatch(g -> g.getName().equals("全部游戏2")));
        assertTrue(games.stream().anyMatch(g -> g.getName().equals("全部游戏3")));
    }

    @Test
    public void whenResetGame_thenMaxScoreIsZero() {
        // given
        Game game = createTestGame("测试重置功能", 500);
        game = entityManager.persist(game);
        entityManager.flush();

        // when
        game.resetGame();
        Game updatedGame = gameRepository.save(game);
        entityManager.flush();

        // then
        Game retrievedGame = entityManager.find(Game.class, game.getId());
        assertEquals(0, retrievedGame.getMaxScore());
    }

    @Test
    public void whenSaveGameWithoutName_thenShouldFail() {
        // given
        Game game = new Game();
        game.setMaxScore(100);

        // then
        assertThrows(Exception.class, () -> {
            // when
            entityManager.persist(game);
            entityManager.flush();
        });
    }
}
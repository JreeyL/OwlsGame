package org.OwlsGame.backend.dao;

import java.util.List;
import java.util.Optional;

import org.OwlsGame.backend.models.Game;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GameRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    // Helper method to create test games
    private Game createTestGame(String name, int maxScore) {
        return new Game(name, maxScore);
    }

    @Test
    public void whenFindByName_thenReturnGame() {
        Game game = createTestGame("Test Game", 100);
        entityManager.persist(game);
        entityManager.flush();

        Optional<Game> found = gameRepository.findByName(game.getName());

        assertTrue(found.isPresent());
        assertEquals(game.getName(), found.get().getName());
        assertEquals(game.getMaxScore(), found.get().getMaxScore());
    }

    @Test
    public void whenFindByNonExistingName_thenReturnEmpty() {
        String nonExistingName = "Non-Existent Game";
        Optional<Game> found = gameRepository.findByName(nonExistingName);
        assertFalse(found.isPresent());
    }

    @Test
    public void whenFindByMaxScore_thenReturnGames() {
        int targetMaxScore = 200;
        Game game1 = createTestGame("Game 1", targetMaxScore);
        Game game2 = createTestGame("Game 2", targetMaxScore);
        Game game3 = createTestGame("Game 3", 300);

        entityManager.persist(game1);
        entityManager.persist(game2);
        entityManager.persist(game3);
        entityManager.flush();

        List<Game> found = gameRepository.findByMaxScore(targetMaxScore);

        assertEquals(2, found.size());
        assertTrue(found.stream().allMatch(g -> g.getMaxScore() == targetMaxScore));
    }

    @Test
    public void whenFindByNonExistingMaxScore_thenReturnEmptyList() {
        int nonExistingMaxScore = 999;
        List<Game> found = gameRepository.findByMaxScore(nonExistingMaxScore);
        assertTrue(found.isEmpty());
    }

    @Test
    public void whenSaveGame_thenGameIsPersisted() {
        Game game = createTestGame("New Game", 150);
        Game savedGame = gameRepository.save(game);
        assertNotNull(savedGame.getId());
        assertEquals(game.getName(), savedGame.getName());
        assertEquals(game.getMaxScore(), savedGame.getMaxScore());
    }

    @Test
    public void whenFindById_thenReturnGame() {
        Game game = createTestGame("ID Query Test", 250);
        game = entityManager.persist(game);
        entityManager.flush();
        Long gameId = game.getId();

        Optional<Game> found = gameRepository.findById(gameId);

        assertTrue(found.isPresent());
        assertEquals(gameId, found.get().getId());
        assertEquals(game.getName(), found.get().getName());
    }

    @Test
    public void whenFindByNonExistingId_thenReturnEmpty() {
        Long nonExistingId = 9999L;
        Optional<Game> found = gameRepository.findById(nonExistingId);
        assertFalse(found.isPresent());
    }

    @Test
    public void whenUpdateGame_thenGameIsUpdated() {
        Game game = createTestGame("Original Game", 300);
        game = entityManager.persist(game);
        entityManager.flush();

        game.setName("Updated Game");
        game.setMaxScore(400);
        Game updatedGame = gameRepository.save(game);
        entityManager.flush();

        Game retrievedGame = entityManager.find(Game.class, game.getId());
        assertEquals("Updated Game", retrievedGame.getName());
        assertEquals(400, retrievedGame.getMaxScore());
    }

    @Test
    public void whenDeleteGame_thenGameIsRemoved() {
        Game game = createTestGame("Game To Delete", 100);
        game = entityManager.persist(game);
        entityManager.flush();
        Long gameId = game.getId();

        gameRepository.deleteById(gameId);
        entityManager.flush();

        Game deletedGame = entityManager.find(Game.class, gameId);
        assertNull(deletedGame);
    }

    @Test
    public void whenFindAll_thenReturnAllGames() {
        Game game1 = createTestGame("All Games 1", 100);
        Game game2 = createTestGame("All Games 2", 200);
        Game game3 = createTestGame("All Games 3", 300);

        entityManager.persist(game1);
        entityManager.persist(game2);
        entityManager.persist(game3);
        entityManager.flush();

        List<Game> games = gameRepository.findAll();

        assertEquals(3, games.size());
        assertTrue(games.stream().anyMatch(g -> g.getName().equals("All Games 1")));
        assertTrue(games.stream().anyMatch(g -> g.getName().equals("All Games 2")));
        assertTrue(games.stream().anyMatch(g -> g.getName().equals("All Games 3")));
    }

    @Test
    public void whenResetGame_thenMaxScoreIsZero() {
        Game game = createTestGame("Test Reset Function", 500);
        game = entityManager.persist(game);
        entityManager.flush();

        game.resetGame();
        Game updatedGame = gameRepository.save(game);
        entityManager.flush();

        Game retrievedGame = entityManager.find(Game.class, game.getId());
        assertEquals(0, retrievedGame.getMaxScore());
    }

    @Test
    public void whenSaveGameWithoutName_thenShouldFail() {
        Game game = new Game();
        game.setMaxScore(100);

        assertThrows(Exception.class, () -> {
            entityManager.persist(game);
            entityManager.flush();
        });
    }
}
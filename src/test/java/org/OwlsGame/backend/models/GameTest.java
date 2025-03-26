package org.OwlsGame.backend.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;
    private final String GAME_NAME = "TestGame";
    private final int MAX_SCORE = 1000;

    @BeforeEach
    void setUp() {
        game = new Game(GAME_NAME, MAX_SCORE);
    }

    @Test
    void testDefaultConstructor() {
        Game defaultGame = new Game();
        assertNotNull(defaultGame);
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals(GAME_NAME, game.getName());
        assertEquals(MAX_SCORE, game.getMaxScore());
    }

    @Test
    void testIdGetterAndSetter() {
        long id = 5L;
        game.setId(id);
        assertEquals(id, game.getId());
    }

    @Test
    void testNameGetterAndSetter() {
        String newName = "NewGame";
        game.setName(newName);
        assertEquals(newName, game.getName());
    }

    @Test
    void testMaxScoreGetterAndSetter() {
        int newMaxScore = 2000;
        game.setMaxScore(newMaxScore);
        assertEquals(newMaxScore, game.getMaxScore());
    }

    @Test
    void testStartGame() {
        // 由于startGame()方法目前没有实际逻辑，只能测试不会抛出异常
        assertDoesNotThrow(() -> game.startGame());
    }

    @Test
    void testCalculateScore() {
        // 测试calculateScore()方法返回预期值（当前实现返回0）
        assertEquals(0, game.calculateScore());
    }

    @Test
    void testResetGame() {
        // 设置一个非零的maxScore
        game.setMaxScore(500);
        assertEquals(500, game.getMaxScore());

        // 调用resetGame()方法
        game.resetGame();

        // 验证maxScore被重置为0
        assertEquals(0, game.getMaxScore());
    }
}
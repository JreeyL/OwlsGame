package org.OwlsGame.backend.service;

import org.OwlsGame.backend.dao.GameRepository;
import org.OwlsGame.backend.models.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceImplTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameServiceImpl gameService;

    private Game testGame;
    private Game anotherGame;

    @BeforeEach
    void setUp() {
        // 设置测试游戏对象
        testGame = new Game("测试游戏", 100);
        testGame.setId(1L);

        anotherGame = new Game("另一个游戏", 200);
        anotherGame.setId(2L);
    }

    @Test
    void whenCreateGame_thenReturnSavedGame() {
        // given
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        // when
        Game savedGame = gameService.createGame(testGame);

        // then
        assertNotNull(savedGame);
        assertEquals(testGame.getName(), savedGame.getName());
        assertEquals(testGame.getMaxScore(), savedGame.getMaxScore());
        verify(gameRepository, times(1)).save(testGame);
    }

    @Test
    void whenGetGameById_thenReturnGame() {
        // given
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));

        // when
        Optional<Game> foundGame = gameService.getGameById(1L);

        // then
        assertTrue(foundGame.isPresent());
        assertEquals(testGame.getId(), foundGame.get().getId());
        assertEquals(testGame.getName(), foundGame.get().getName());
        verify(gameRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetGameByNonExistingId_thenReturnEmpty() {
        // given
        when(gameRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<Game> foundGame = gameService.getGameById(999L);

        // then
        assertFalse(foundGame.isPresent());
        verify(gameRepository, times(1)).findById(999L);
    }

    @Test
    void whenGetAllGames_thenReturnGamesList() {
        // given
        List<Game> games = Arrays.asList(testGame, anotherGame);
        when(gameRepository.findAll()).thenReturn(games);

        // when
        List<Game> foundGames = gameService.getAllGames();

        // then
        assertEquals(2, foundGames.size());
        verify(gameRepository, times(1)).findAll();
    }

    @Test
    void whenUpdateGame_thenReturnUpdatedGame() {
        // given
        when(gameRepository.existsById(1L)).thenReturn(true);
        testGame.setName("更新后的游戏名");
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        // when
        Game updatedGame = gameService.updateGame(testGame);

        // then
        assertEquals("更新后的游戏名", updatedGame.getName());
        verify(gameRepository, times(1)).existsById(1L);
        verify(gameRepository, times(1)).save(testGame);
    }

    @Test
    void whenUpdateNonExistingGame_thenThrowException() {
        // given
        when(gameRepository.existsById(999L)).thenReturn(false);
        Game nonExistingGame = new Game("不存在的游戏", 300);
        nonExistingGame.setId(999L);

        // when & then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gameService.updateGame(nonExistingGame);
        });

        assertEquals("Game not found with id: 999", exception.getMessage());
        verify(gameRepository, times(1)).existsById(999L);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void whenDeleteGame_thenRepositoryMethodIsCalled() {
        // given
        doNothing().when(gameRepository).deleteById(1L);

        // when
        gameService.deleteGame(1L);

        // then
        verify(gameRepository, times(1)).deleteById(1L);
    }

    @Test
    void whenFindByName_thenReturnGame() {
        // given
        String gameName = "测试游戏";
        when(gameRepository.findByName(gameName)).thenReturn(Optional.of(testGame));

        // when
        Optional<Game> foundGame = gameService.findByName(gameName);

        // then
        assertTrue(foundGame.isPresent());
        assertEquals(gameName, foundGame.get().getName());
        verify(gameRepository, times(1)).findByName(gameName);
    }

    @Test
    void whenFindByNonExistingName_thenReturnEmpty() {
        // given
        String nonExistingName = "不存在的游戏";
        when(gameRepository.findByName(nonExistingName)).thenReturn(Optional.empty());

        // when
        Optional<Game> foundGame = gameService.findByName(nonExistingName);

        // then
        assertFalse(foundGame.isPresent());
        verify(gameRepository, times(1)).findByName(nonExistingName);
    }

    @Test
    void whenFindByMaxScore_thenReturnGamesList() {
        // given
        int maxScore = 200;
        List<Game> games = List.of(anotherGame);
        when(gameRepository.findByMaxScore(maxScore)).thenReturn(games);

        // when
        List<Game> foundGames = gameService.findByMaxScore(maxScore);

        // then
        assertEquals(1, foundGames.size());
        assertEquals(maxScore, foundGames.get(0).getMaxScore());
        verify(gameRepository, times(1)).findByMaxScore(maxScore);
    }

    @Test
    void whenFindByNonExistingMaxScore_thenReturnEmptyList() {
        // given
        int nonExistingMaxScore = 999;
        when(gameRepository.findByMaxScore(nonExistingMaxScore)).thenReturn(Collections.emptyList());

        // when
        List<Game> foundGames = gameService.findByMaxScore(nonExistingMaxScore);

        // then
        assertTrue(foundGames.isEmpty());
        verify(gameRepository, times(1)).findByMaxScore(nonExistingMaxScore);
    }

    @Test
    void whenResetGame_thenGameIsResetAndSaved() {
        // given
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        // when
        gameService.resetGame(1L);

        // then
        assertEquals(0, testGame.getMaxScore()); // Game.resetGame() sets maxScore to 0
        verify(gameRepository, times(1)).findById(1L);
        verify(gameRepository, times(1)).save(testGame);
    }

    @Test
    void whenResetNonExistingGame_thenThrowException() {
        // given
        when(gameRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gameService.resetGame(999L);
        });

        assertEquals("Game not found", exception.getMessage());
        verify(gameRepository, times(1)).findById(999L);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void whenGetAllGamesReturnsEmpty_thenReturnEmptyList() {
        // given
        when(gameRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<Game> allGames = gameService.getAllGames();

        // then
        assertTrue(allGames.isEmpty());
        verify(gameRepository, times(1)).findAll();
    }
}
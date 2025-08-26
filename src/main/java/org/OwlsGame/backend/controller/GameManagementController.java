package org.OwlsGame.backend.controller;

import org.OwlsGame.backend.models.Game;
import org.OwlsGame.backend.models.Score;
import org.OwlsGame.backend.models.User;
import org.OwlsGame.backend.service.GameService;
import org.OwlsGame.backend.service.ScoreService;
import org.OwlsGame.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/management")  // 修改路径以避免冲突
public class GameManagementController {

    @Autowired
    private GameService gameService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private UserService userService;

    // 获取所有游戏
    @GetMapping("/games")
    public List<Game> getAllGames() {
        return gameService.getAllGames();
    }

    // 获取指定游戏详情
    @GetMapping("/games/{gameId}")
    public ResponseEntity<Game> getGameById(@PathVariable Long gameId) {
        return gameService.getGameById(gameId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 创建新游戏
    @PostMapping("/games")
    public Game createGame(@RequestBody Game game) {
        return gameService.createGame(game);
    }

    // 更新游戏设置
    @PutMapping("/games/{gameId}")
    public ResponseEntity<Game> updateGame(@PathVariable Long gameId, @RequestBody Game game) {
        if (!gameService.getGameById(gameId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        game.setId(gameId);
        return ResponseEntity.ok(gameService.updateGame(game));
    }

    // 获取指定游戏的所有分数
    @GetMapping("/games/{gameId}/scores")
    public List<Map<String, Object>> getGameScores(@PathVariable Long gameId) {
        List<Score> scores = scoreService.getScoresByGameId(gameId.intValue());

        return scores.stream().map(score -> {
            Map<String, Object> scoreData = new HashMap<>();
            scoreData.put("id", score.getId());
            scoreData.put("userId", score.getUserId());
            scoreData.put("email", score.getEmail());
            scoreData.put("score_value", score.getScoreValue());
            scoreData.put("timestamp", score.getTimestamp());
            scoreData.put("play_time", score.getPlayTime());

            // 获取用户名
            userService.getUserById((long) score.getUserId())
                    .ifPresent(user -> {
                        scoreData.put("first_name", user.getFirstname());
                        scoreData.put("last_name", user.getLastname());
                    });

            return scoreData;
        }).collect(Collectors.toList());
    }

    // 获取游戏排行榜 (前10名)
    @GetMapping("/games/{gameId}/leaderboard")
    public List<Map<String, Object>> getGameLeaderboard(@PathVariable Long gameId) {
        List<Score> topScores = scoreService.getTopNScoresByGame(gameId.intValue(), 10);

        return topScores.stream().map(score -> {
            Map<String, Object> scoreData = new HashMap<>();
            scoreData.put("id", score.getId());
            scoreData.put("user_id", score.getUserId());
            scoreData.put("email", score.getEmail());
            scoreData.put("score_value", score.getScoreValue());
            scoreData.put("timestamp", score.getTimestamp());

            // 获取用户名
            userService.getUserById((long) score.getUserId())
                    .ifPresent(user -> {
                        scoreData.put("first_name", user.getFirstname());
                        scoreData.put("last_name", user.getLastname());
                    });

            return scoreData;
        }).collect(Collectors.toList());
    }

    // 重置游戏所有分数 (危险操作，需要添加权限控制)
    @DeleteMapping("/games/{gameId}/scores")
    public ResponseEntity<?> resetGameScores(@PathVariable Long gameId) {
        List<Score> scores = scoreService.getScoresByGameId(gameId.intValue());
        for (Score score : scores) {
            scoreService.deleteScoreById(score.getId());
        }
        return ResponseEntity.ok().body(Map.of("message", "All scores for game ID " + gameId + " have been reset"));
    }
}
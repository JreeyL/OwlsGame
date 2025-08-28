package org.OwlsGame.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.OwlsGame.backend.models.Game;
import org.OwlsGame.backend.models.Score;
import org.OwlsGame.backend.service.GameService;
import org.OwlsGame.backend.service.ScoreService;
import org.OwlsGame.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LeaderboardController {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardController.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ScoreService scoreService;
    
    @Autowired
    private GameService gameService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/leaderboard")
    public String showLeaderboard(@RequestParam(defaultValue = "True/False") String game, Model model) {
        try {
            logger.info("Attempting to fetch leaderboard for game: {}", game);
            
            // 先尝试简单查询验证数据是否存在
            String testSql = "SELECT COUNT(*) FROM scores";
            Integer totalScores = jdbcTemplate.queryForObject(testSql, Integer.class);
            logger.info("Total scores in database: {}", totalScores);
            
            // 查询指定游戏的分数
            String sql = "SELECT s.id, s.user_id, s.score_value, s.timestamp, s.play_time, " +
                        "u.email, u.first_name, u.last_name, g.name as game_name " +
                        "FROM scores s " +
                        "INNER JOIN users u ON s.user_id = u.id " +
                        "INNER JOIN games g ON s.game_id = g.id " +
                        "WHERE g.name = ? " +
                        "ORDER BY s.score_value DESC, s.timestamp ASC " +
                        "LIMIT 5";
            
            List<Map<String, Object>> rawResults = jdbcTemplate.queryForList(sql, game);
            logger.info("Found {} results using JDBC", rawResults.size());
            
            List<Map<String, Object>> topPlayers = rawResults.stream()
                    .map(this::normalizePlayerData)
                    .collect(Collectors.toList());
            
            model.addAttribute("topPlayers", topPlayers);
            model.addAttribute("currentGame", game);
        } catch (Exception e) {
            // 如果JDBC查询失败，使用JPA服务作为fallback
            logger.error("JDBC query failed, falling back to JPA: {}", e.getMessage());
            try {
                List<Map<String, Object>> topPlayers = getTopPlayersUsingJPA(game);
                model.addAttribute("topPlayers", topPlayers);
                model.addAttribute("currentGame", game);
                logger.info("JPA fallback successful, found {} players", topPlayers.size());
            } catch (Exception fallbackError) {
                logger.error("JPA fallback also failed: {}", fallbackError.getMessage());
                model.addAttribute("topPlayers", new ArrayList<>());
                model.addAttribute("currentGame", game);
            }
        }
        return "leaderboard";
    }
    
    // Fallback using JPA services to avoid JDBC case/driver issues
    private List<Map<String, Object>> getTopPlayersUsingJPA(String gameName) {
        logger.info("Using JPA fallback to fetch scores for game: {}", gameName);
        
        // 获取所有分数
        List<Score> allScores = scoreService.getAllScores();
        
        // 过滤指定游戏的分数并按分数排序，取前5名
        return allScores.stream()
                .filter(score -> {
                    // 通过gameId获取游戏名称
                    return gameService.getGameById((long) score.getGameId())
                            .map(Game::getName)
                            .map(name -> name.equals(gameName))
                            .orElse(false);
                })
                .sorted(Comparator.comparing(Score::getScoreValue, Comparator.reverseOrder())
                        .thenComparing(Score::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(5)
                .map(this::convertScoreToPlayerData)
                .collect(Collectors.toList());
    }
    
    // 将Score对象转换为前端需要的格式
    private Map<String, Object> convertScoreToPlayerData(Score score) {
        Map<String, Object> player = new HashMap<>();
        player.put("id", score.getId());
        player.put("email", score.getEmail());
        player.put("scoreValue", score.getScoreValue());
        player.put("playTime", score.getPlayTime());
        
        // 获取游戏名称
        String gameName = gameService.getGameById((long) score.getGameId())
                .map(Game::getName)
                .orElse("Unknown");
        player.put("gameName", gameName);
        
        // 尝试获取用户信息
        if (score.getUserId() != 0) {
            userService.getUserById((long) score.getUserId()).ifPresent(user -> {
                player.put("firstName", user.getFirstname());
                player.put("lastName", user.getLastname());
            });
        }
        
        // 如果没有firstName和lastName，设置默认值
        player.putIfAbsent("firstName", "");
        player.putIfAbsent("lastName", "");
        
        // 格式化时间戳
        if (score.getTimestamp() != null) {
            player.put("timestamp", score.getTimestamp().toInstant().toString());
        } else {
            player.put("timestamp", "");
        }
        
        return player;
    }
    
    // 数据格式化方法，与AdminApiController保持一致
    private Map<String, Object> normalizePlayerData(Map<String, Object> row) {
        Map<String, Object> player = new HashMap<>();
        player.put("id", row.get("id"));
        player.put("email", row.get("email"));
        player.put("firstName", row.get("first_name"));
        player.put("lastName", row.get("last_name"));
        player.put("scoreValue", row.get("score_value"));
        player.put("playTime", row.get("play_time"));
        player.put("gameName", row.get("game_name"));
        
        // 格式化时间戳
        Object ts = row.get("timestamp");
        if (ts instanceof Timestamp) {
            player.put("timestamp", ((Timestamp) ts).toInstant().toString());
        } else if (ts != null) {
            player.put("timestamp", ts.toString());
        } else {
            player.put("timestamp", "");
        }
        
        return player;
    }
}

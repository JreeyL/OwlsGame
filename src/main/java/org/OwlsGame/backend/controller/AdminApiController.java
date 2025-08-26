package org.OwlsGame.backend.controller;

import org.OwlsGame.backend.dao.ScoreRepository;
import org.OwlsGame.backend.dao.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    @Autowired
    private ScoreRepository scoreRepository;
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Get all scores with game information and statistics
     * Addresses the field mapping and type conversion issues
     */
    @GetMapping("/scores")
    public Map<String, Object> getAllScores() {
        try {
            // Use proper SQL JOIN to get game names with scores
            String sql = """
                SELECT s.id as score_id,
                       s.user_id,
                       s.game_id,
                       s.score_value,
                       s.play_time,
                       s.timestamp,
                       s.email,
                       g.name as game_name
                FROM scores s 
                LEFT JOIN games g ON s.game_id = g.id
                ORDER BY s.timestamp DESC
                """;
            
            List<Map<String, Object>> rawResults = jdbcTemplate.queryForList(sql);
            List<Map<String, Object>> scores = new ArrayList<>();
            
            // Convert and map fields with safe type conversion
            for (Map<String, Object> row : rawResults) {
                Map<String, Object> score = new HashMap<>();
                
                // Safe type conversion for different database return types
                score.put("id", safeConvertToInteger(row.get("score_id")));
                score.put("userId", safeConvertToInteger(row.get("user_id")));
                score.put("gameId", safeConvertToInteger(row.get("game_id")));
                
                // Map database field names to frontend field names
                score.put("scoreValue", safeConvertToInteger(row.get("score_value")));
                score.put("playTime", safeConvertToInteger(row.get("play_time")));
                score.put("gameName", safeConvertToString(row.get("game_name"), "Unknown"));
                
                score.put("timestamp", row.get("timestamp"));
                score.put("email", safeConvertToString(row.get("email"), ""));
                
                scores.add(score);
            }
            
            // Calculate statistics based on correct score values
            Map<String, Object> statistics = calculateStatistics(scores);
            
            Map<String, Object> response = new HashMap<>();
            response.put("scores", scores);
            response.put("statistics", statistics);
            
            return response;
            
        } catch (Exception e) {
            // Fallback in case of database issues
            Map<String, Object> response = new HashMap<>();
            response.put("scores", new ArrayList<>());
            response.put("statistics", getEmptyStatistics());
            response.put("error", "Failed to fetch scores: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * Safe type conversion for Integer values
     * Handles different database return types (H2 vs MySQL)
     */
    private Integer safeConvertToInteger(Object value) {
        if (value == null) {
            return 0;
        }
        
        if (value instanceof Integer) {
            return (Integer) value;
        }
        
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        
        return 0;
    }
    
    /**
     * Safe type conversion for String values
     */
    private String safeConvertToString(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }
    
    /**
     * Calculate statistics based on correct score values
     */
    private Map<String, Object> calculateStatistics(List<Map<String, Object>> scores) {
        Map<String, Object> stats = new HashMap<>();
        
        if (scores.isEmpty()) {
            return getEmptyStatistics();
        }
        
        int totalScores = scores.size();
        int totalPoints = 0;
        int maxScore = 0;
        Set<String> uniquePlayers = new HashSet<>();
        
        for (Map<String, Object> score : scores) {
            Integer scoreValue = (Integer) score.get("scoreValue");
            if (scoreValue != null) {
                totalPoints += scoreValue;
                maxScore = Math.max(maxScore, scoreValue);
            }
            
            String email = (String) score.get("email");
            if (email != null && !email.isEmpty()) {
                uniquePlayers.add(email);
            }
        }
        
        double averageScore = totalScores > 0 ? (double) totalPoints / totalScores : 0.0;
        
        stats.put("totalScores", totalScores);
        stats.put("averageScore", Math.round(averageScore * 100.0) / 100.0); // Round to 2 decimal places
        stats.put("maxScore", maxScore);
        stats.put("totalPlayers", uniquePlayers.size());
        
        return stats;
    }
    
    /**
     * Get empty statistics for error cases
     */
    private Map<String, Object> getEmptyStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalScores", 0);
        stats.put("averageScore", 0.0);
        stats.put("maxScore", 0);
        stats.put("totalPlayers", 0);
        return stats;
    }
    
    /**
     * Get scores by game ID with proper field mapping
     */
    @GetMapping("/scores/game/{gameId}")
    public Map<String, Object> getScoresByGameId(@PathVariable Integer gameId) {
        try {
            String sql = """
                SELECT s.id as score_id,
                       s.user_id,
                       s.game_id,
                       s.score_value,
                       s.play_time,
                       s.timestamp,
                       s.email,
                       g.name as game_name
                FROM scores s 
                LEFT JOIN games g ON s.game_id = g.id
                WHERE s.game_id = ?
                ORDER BY s.score_value DESC, s.timestamp ASC
                """;
            
            List<Map<String, Object>> rawResults = jdbcTemplate.queryForList(sql, gameId);
            List<Map<String, Object>> scores = new ArrayList<>();
            
            for (Map<String, Object> row : rawResults) {
                Map<String, Object> score = new HashMap<>();
                score.put("id", safeConvertToInteger(row.get("score_id")));
                score.put("userId", safeConvertToInteger(row.get("user_id")));
                score.put("gameId", safeConvertToInteger(row.get("game_id")));
                score.put("scoreValue", safeConvertToInteger(row.get("score_value")));
                score.put("playTime", safeConvertToInteger(row.get("play_time")));
                score.put("gameName", safeConvertToString(row.get("game_name"), "Unknown"));
                score.put("timestamp", row.get("timestamp"));
                score.put("email", safeConvertToString(row.get("email"), ""));
                
                scores.add(score);
            }
            
            Map<String, Object> statistics = calculateStatistics(scores);
            
            Map<String, Object> response = new HashMap<>();
            response.put("scores", scores);
            response.put("statistics", statistics);
            
            return response;
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("scores", new ArrayList<>());
            response.put("statistics", getEmptyStatistics());
            response.put("error", "Failed to fetch scores for game: " + e.getMessage());
            return response;
        }
    }
}
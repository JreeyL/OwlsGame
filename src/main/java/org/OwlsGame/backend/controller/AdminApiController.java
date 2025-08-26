package org.OwlsGame.backend.controller;

import org.OwlsGame.backend.service.ScoreService;
import org.OwlsGame.backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    @Autowired
    private ScoreService scoreService;
    
    @Autowired
    private GameService gameService;
    
    @Autowired
    private EntityManager entityManager;

    @GetMapping("/scores")
    public ResponseEntity<Map<String, Object>> getAdminScores() {
        try {
            // Use JOIN query to get scores with game names
            String sql = "SELECT s.id, s.score_value, s.play_time, s.email, s.timestamp, " +
                        "COALESCE(g.name, 'Unknown') as game_name " +
                        "FROM scores s LEFT JOIN games g ON s.game_id = g.id " +
                        "ORDER BY s.timestamp DESC";
            
            Query query = entityManager.createNativeQuery(sql);
            List<Object[]> results = query.getResultList();
            
            List<Map<String, Object>> scoresList = new ArrayList<>();
            int totalScore = 0;
            int totalPlayTime = 0;
            int maxScore = 0;
            
            for (Object[] row : results) {
                Map<String, Object> scoreData = new HashMap<>();
                
                // Safe type conversion for H2 database
                scoreData.put("id", safeConvertToInt(row[0]));
                
                int scoreValue = safeConvertToInt(row[1]);
                scoreData.put("scoreValue", scoreValue);
                totalScore += scoreValue;
                maxScore = Math.max(maxScore, scoreValue);
                
                int playTime = safeConvertToInt(row[2]);
                scoreData.put("playTime", playTime);
                totalPlayTime += playTime;
                
                scoreData.put("email", row[3] != null ? row[3].toString() : "");
                scoreData.put("timestamp", row[4] != null ? row[4].toString() : "");
                scoreData.put("gameName", row[5] != null ? row[5].toString() : "Unknown");
                
                scoresList.add(scoreData);
            }
            
            // Calculate statistics
            Map<String, Object> response = new HashMap<>();
            response.put("scores", scoresList);
            response.put("totalCount", scoresList.size());
            response.put("averageScore", scoresList.size() > 0 ? totalScore / (double) scoresList.size() : 0);
            response.put("maxScore", maxScore);
            response.put("totalPlayTime", totalPlayTime);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Log the exception for debugging
            System.err.println("Error in getAdminScores: " + e.getMessage());
            e.printStackTrace();
            
            // Return error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch scores data");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Safe type conversion method to handle H2 database Number type conversion
     */
    private int safeConvertToInt(Object value) {
        if (value == null) {
            return 0;
        }
        
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Long) {
            return ((Long) value).intValue();
        } else if (value instanceof BigInteger) {
            return ((BigInteger) value).intValue();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).intValue();
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                System.err.println("Failed to convert value to int: " + value + " (type: " + value.getClass().getName() + ")");
                return 0;
            }
        }
    }
}
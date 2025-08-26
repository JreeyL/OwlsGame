package org.OwlsGame.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class AdminApiControllerTest {

    @Autowired
    private AdminApiController adminApiController;

    @Test
    public void testGetAdminScores_ReturnsCorrectData() {
        // Call the controller method directly
        ResponseEntity<Map<String, Object>> response = adminApiController.getAdminScores();

        // Verify response status
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Response should be successful");
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody, "Response body should not be null");

        // Verify the response structure
        assertTrue(responseBody.containsKey("scores"), "Response should contain 'scores' key");
        assertTrue(responseBody.containsKey("totalCount"), "Response should contain 'totalCount' key");
        assertTrue(responseBody.containsKey("averageScore"), "Response should contain 'averageScore' key");
        assertTrue(responseBody.containsKey("maxScore"), "Response should contain 'maxScore' key");
        assertTrue(responseBody.containsKey("totalPlayTime"), "Response should contain 'totalPlayTime' key");

        // Verify scores data
        List<Map<String, Object>> scores = (List<Map<String, Object>>) responseBody.get("scores");
        assertNotNull(scores, "Scores list should not be null");
        assertTrue(scores.size() > 0, "Should have at least one score record");

        // Verify data from data.sql is loaded correctly
        boolean foundTargetScore = false;
        for (Map<String, Object> score : scores) {
            assertNotNull(score.get("scoreValue"), "Score value should not be null");
            assertNotNull(score.get("playTime"), "Play time should not be null");
            assertNotNull(score.get("gameName"), "Game name should not be null");
            assertNotNull(score.get("email"), "Email should not be null");
            
            // Check if we find the specific test data from data.sql
            if ("jreeylee92@outlook.com".equals(score.get("email")) && 
                "True/False".equals(score.get("gameName"))) {
                foundTargetScore = true;
                
                // Verify that the scores are not zero (the original problem)
                int scoreValue = (Integer) score.get("scoreValue");
                int playTime = (Integer) score.get("playTime");
                
                assertTrue(scoreValue > 0, "Score value should be greater than 0, but was: " + scoreValue);
                assertTrue(playTime > 0, "Play time should be greater than 0, but was: " + playTime);
                assertNotEquals("Unknown", score.get("gameName"), "Game name should not be 'Unknown'");
                assertEquals("True/False", score.get("gameName"), "Game name should be 'True/False'");
            }
        }
        
        assertTrue(foundTargetScore, "Should find the target score data from data.sql");

        // Verify statistics are calculated correctly
        Integer totalCount = (Integer) responseBody.get("totalCount");
        Double averageScore = (Double) responseBody.get("averageScore");
        Integer maxScore = (Integer) responseBody.get("maxScore");
        Integer totalPlayTime = (Integer) responseBody.get("totalPlayTime");

        assertTrue(totalCount > 0, "Total count should be greater than 0");
        assertTrue(averageScore >= 0, "Average score should be non-negative");
        assertTrue(maxScore >= 0, "Max score should be non-negative");
        assertTrue(totalPlayTime >= 0, "Total play time should be non-negative");
    }

    @Test
    public void testGetAdminScores_ValidatesDataTypes() {
        ResponseEntity<Map<String, Object>> response = adminApiController.getAdminScores();
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        // Verify data types
        assertTrue(responseBody.get("totalCount") instanceof Integer, "Total count should be an integer");
        assertTrue(responseBody.get("averageScore") instanceof Number, "Average score should be a number");
        assertTrue(responseBody.get("maxScore") instanceof Integer, "Max score should be an integer");
        assertTrue(responseBody.get("totalPlayTime") instanceof Integer, "Total play time should be an integer");

        List<Map<String, Object>> scores = (List<Map<String, Object>>) responseBody.get("scores");
        if (!scores.isEmpty()) {
            Map<String, Object> firstScore = scores.get(0);
            assertTrue(firstScore.get("scoreValue") instanceof Integer, "Score value should be an integer");
            assertTrue(firstScore.get("playTime") instanceof Integer, "Play time should be an integer");
            assertTrue(firstScore.get("gameName") instanceof String, "Game name should be a string");
            assertTrue(firstScore.get("email") instanceof String, "Email should be a string");
        }
    }

    @Test
    public void testGetAdminScores_HandlesCorrectStructure() {
        ResponseEntity<Map<String, Object>> response = adminApiController.getAdminScores();
        assertTrue(response.getStatusCode().is2xxSuccessful());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        // Verify that the response has the expected structure
        assertNotNull(responseBody.get("scores"));
        assertNotNull(responseBody.get("totalCount"));
        assertNotNull(responseBody.get("averageScore"));
        assertNotNull(responseBody.get("maxScore"));
        assertNotNull(responseBody.get("totalPlayTime"));
    }
}
package org.OwlsGame.backend.controller;

import org.OwlsGame.backend.dao.GameRepository;
import org.OwlsGame.backend.dao.ScoreRepository;
import org.OwlsGame.backend.models.Game;
import org.OwlsGame.backend.models.Score;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

/**
 * Integration test to demonstrate the complete fix for the score management page issue
 * This test validates that the exact problem described in the issue is now resolved
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AdminApiController_ProblemSolutionTest {

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private AdminApiController adminApiController;

    /**
     * Test that demonstrates the exact fix for the problem described:
     * - Database has: game_id=1, score_value=2, play_time=7, email=jreeylee92@outlook.com
     * - Game has: id=1, name="True/False", max_score=1000
     * - Expected display: Score=2, GameTime=7sec, GameName="True/False", Stats=correct
     */
    @Test
    @Transactional
    public void testProblemSolution_ExactScenario() {
        // Setup: Create exact data from problem description
        scoreRepository.deleteAll();
        gameRepository.deleteAll();

        // Create game with exact data: id=1, name="True/False", max_score=1000
        Game game = new Game("True/False", 1000);
        game = gameRepository.save(game);
        
        // Create score with exact data: game_id=1, score_value=2, play_time=7, email=jreeylee92@outlook.com
        Score score = new Score(
            1, // userId
            game.getId().intValue(), // gameId (this will be 1 after save)
            2, // scoreValue 
            Timestamp.from(Instant.now()), // timestamp
            "jreeylee92@outlook.com", // email
            7 // playTime
        );
        score = scoreRepository.save(score);

        // Call the AdminApiController that was missing/broken
        Map<String, Object> response = adminApiController.getAllScores();
        
        // Verify the response structure exists
        assert response.containsKey("scores") : "Response should contain scores";
        assert response.containsKey("statistics") : "Response should contain statistics";
        
        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> scores = (java.util.List<Map<String, Object>>) response.get("scores");
        
        // Verify we get the score data (was empty/null before)
        assert !scores.isEmpty() : "Should have score data";
        
        Map<String, Object> scoreData = scores.get(0);
        
        // MAIN FIX VERIFICATION: The exact problems mentioned in the issue
        
        // 1. Score: was showing 0, should show 2
        Integer actualScoreValue = (Integer) scoreData.get("scoreValue");
        assert actualScoreValue != null && actualScoreValue.equals(2) : 
            "Score should be 2, but was: " + actualScoreValue;
        
        // 2. Game time: was showing 0 sec, should show 7 sec
        Integer actualPlayTime = (Integer) scoreData.get("playTime");
        assert actualPlayTime != null && actualPlayTime.equals(7) : 
            "Play time should be 7, but was: " + actualPlayTime;
        
        // 3. Game name: was showing "Unknown", should show "True/False"
        String actualGameName = (String) scoreData.get("gameName");
        assert "True/False".equals(actualGameName) : 
            "Game name should be 'True/False', but was: " + actualGameName;
        
        // 4. Email should be correctly retrieved
        String actualEmail = (String) scoreData.get("email");
        assert "jreeylee92@outlook.com".equals(actualEmail) : 
            "Email should be 'jreeylee92@outlook.com', but was: " + actualEmail;
        
        // 5. Statistics: were showing averageScore=0, maxScore=0, should show correct values
        @SuppressWarnings("unchecked")
        Map<String, Object> statistics = (Map<String, Object>) response.get("statistics");
        
        Double actualAverage = (Double) statistics.get("averageScore");
        assert actualAverage != null && actualAverage.equals(2.0) : 
            "Average score should be 2.0, but was: " + actualAverage;
        
        Integer actualMaxScore = (Integer) statistics.get("maxScore");
        assert actualMaxScore != null && actualMaxScore.equals(2) : 
            "Max score should be 2, but was: " + actualMaxScore;
        
        Integer actualTotalScores = (Integer) statistics.get("totalScores");
        assert actualTotalScores != null && actualTotalScores.equals(1) : 
            "Total scores should be 1, but was: " + actualTotalScores;
        
        Integer actualTotalPlayers = (Integer) statistics.get("totalPlayers");
        assert actualTotalPlayers != null && actualTotalPlayers.equals(1) : 
            "Total players should be 1, but was: " + actualTotalPlayers;
        
        // SUCCESS: All the problems from the issue description are now fixed!
        System.out.println("✅ SUCCESS: All problems from issue description are now fixed!");
        System.out.println("   - Score: " + actualScoreValue + " (was 0, now correct)");
        System.out.println("   - Play time: " + actualPlayTime + " sec (was 0, now correct)");
        System.out.println("   - Game name: '" + actualGameName + "' (was 'Unknown', now correct)");
        System.out.println("   - Average score: " + actualAverage + " (was 0, now correct)");
        System.out.println("   - Max score: " + actualMaxScore + " (was 0, now correct)");
    }
}
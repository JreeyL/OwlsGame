package org.OwlsGame.backend.controller;

import org.OwlsGame.backend.dao.GameRepository;
import org.OwlsGame.backend.dao.ScoreRepository;
import org.OwlsGame.backend.models.Game;
import org.OwlsGame.backend.models.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AdminApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private AdminApiController adminApiController;

    private Game testGame;
    private Score testScore;

    @BeforeEach
    @Transactional
    public void setUp() {
        // Clean up existing data
        scoreRepository.deleteAll();
        gameRepository.deleteAll();

        // Create test game matching the problem description
        testGame = new Game("True/False", 1000);
        testGame = gameRepository.save(testGame);

        // Create test score matching the problem description
        // Expected: score_value=2, play_time=7, email=jreeylee92@outlook.com
        testScore = new Score(
            1, // userId
            testGame.getId().intValue(), // gameId (convert Long to int)
            2, // scoreValue
            Timestamp.from(Instant.now()), // timestamp
            "jreeylee92@outlook.com", // email
            7 // playTime
        );
        testScore = scoreRepository.save(testScore);
    }

    @Test
    public void testGetAllScores_ReturnsCorrectData() throws Exception {
        mockMvc.perform(get("/api/admin/scores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scores", hasSize(1)))
                .andExpect(jsonPath("$.scores[0].scoreValue", is(2)))  // Should be 2, not 0
                .andExpect(jsonPath("$.scores[0].playTime", is(7)))    // Should be 7, not 0
                .andExpect(jsonPath("$.scores[0].gameName", is("True/False"))) // Should be "True/False", not "Unknown"
                .andExpect(jsonPath("$.scores[0].email", is("jreeylee92@outlook.com")))
                .andExpect(jsonPath("$.scores[0].gameId", is(testGame.getId().intValue())));
    }

    @Test
    public void testGetAllScores_StatisticsCorrect() throws Exception {
        mockMvc.perform(get("/api/admin/scores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statistics.totalScores", is(1)))
                .andExpect(jsonPath("$.statistics.averageScore", is(2.0)))  // Should be 2.0, not 0
                .andExpect(jsonPath("$.statistics.maxScore", is(2)))        // Should be 2, not 0
                .andExpect(jsonPath("$.statistics.totalPlayers", is(1)));
    }

    @Test
    public void testGetScoresByGameId_ReturnsCorrectData() throws Exception {
        mockMvc.perform(get("/api/admin/scores/game/" + testGame.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scores", hasSize(1)))
                .andExpect(jsonPath("$.scores[0].scoreValue", is(2)))
                .andExpect(jsonPath("$.scores[0].playTime", is(7)))
                .andExpect(jsonPath("$.scores[0].gameName", is("True/False")));
    }

    @Test
    public void testFieldMapping_DatabaseToFrontend() {
        // Test the safe conversion methods directly
        Map<String, Object> result = adminApiController.getAllScores();
        
        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> scores = (java.util.List<Map<String, Object>>) result.get("scores");
        
        assert !scores.isEmpty() : "Should have test score data";
        
        Map<String, Object> score = scores.get(0);
        
        // Verify field mapping from database columns to frontend fields
        assert score.containsKey("scoreValue") : "Should map score_value to scoreValue";
        assert score.containsKey("playTime") : "Should map play_time to playTime";
        assert score.containsKey("gameName") : "Should map game name via JOIN";
        
        // Verify actual values match expected
        assert score.get("scoreValue").equals(2) : "Score value should be 2, got: " + score.get("scoreValue");
        assert score.get("playTime").equals(7) : "Play time should be 7, got: " + score.get("playTime");
        assert "True/False".equals(score.get("gameName")) : "Game name should be True/False, got: " + score.get("gameName");
    }

    @Test
    public void testEmptyDatabase_ReturnsEmptyResults() throws Exception {
        // Clean up data for empty test
        scoreRepository.deleteAll();
        gameRepository.deleteAll();

        mockMvc.perform(get("/api/admin/scores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scores", hasSize(0)))
                .andExpect(jsonPath("$.statistics.totalScores", is(0)))
                .andExpect(jsonPath("$.statistics.averageScore", is(0.0)))
                .andExpect(jsonPath("$.statistics.maxScore", is(0)))
                .andExpect(jsonPath("$.statistics.totalPlayers", is(0)));
    }

    @Test
    public void testMultipleScores_CorrectStatistics() throws Exception {
        // Add another score to test statistics calculation
        Score secondScore = new Score(
            2, // different userId
            testGame.getId().intValue(),
            5, // different score value
            Timestamp.from(Instant.now()),
            "test2@example.com",
            10 // different play time
        );
        scoreRepository.save(secondScore);

        mockMvc.perform(get("/api/admin/scores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scores", hasSize(2)))
                .andExpect(jsonPath("$.statistics.totalScores", is(2)))
                .andExpect(jsonPath("$.statistics.averageScore", is(3.5)))  // (2 + 5) / 2 = 3.5
                .andExpect(jsonPath("$.statistics.maxScore", is(5)))        // max of 2 and 5
                .andExpect(jsonPath("$.statistics.totalPlayers", is(2)));   // two unique emails
    }
}
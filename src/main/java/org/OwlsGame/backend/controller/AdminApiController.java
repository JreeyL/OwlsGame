package org.OwlsGame.backend.controller;

import org.OwlsGame.backend.dao.GameRepository;
import org.OwlsGame.backend.dao.ScoreRepository;
import org.OwlsGame.backend.dao.UserRepository;
import org.OwlsGame.backend.models.Game;
import org.OwlsGame.backend.models.Score;
import org.OwlsGame.backend.service.GameService;
import org.OwlsGame.backend.service.ScoreService;
import org.OwlsGame.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private static final Logger logger = Logger.getLogger(AdminApiController.class.getName());
    // Return ISO instant strings for timestamps so frontend can parse them reliably
    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ISO_INSTANT;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private UserService userService;

    // -------------------- Games CRUD --------------------

    @GetMapping("/games")
    public List<Game> getAllGames() {
        logger.info("Fetching all games");
        return gameRepository.findAll();
    }

    @GetMapping("/games/{gameId}")
    public ResponseEntity<Game> getGameById(@PathVariable Long gameId) {
        logger.info("Fetching game details: " + gameId);
        return gameService.getGameById(gameId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/games")
    public Game createGame(@RequestBody Game game) {
        logger.info("Creating new game: " + game.getName());
        return gameService.createGame(game);
    }

    @PutMapping("/games/{gameId}")
    public ResponseEntity<Game> updateGame(@PathVariable Long gameId, @RequestBody Game game) {
        logger.info("Updating game: " + gameId);
        if (!gameService.getGameById(gameId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        game.setId(gameId);
        return ResponseEntity.ok(gameService.updateGame(game));
    }

    @DeleteMapping("/games/{gameId}")
    public ResponseEntity<?> deleteGame(@PathVariable Long gameId) {
        logger.info("Deleting game: " + gameId);
        gameService.deleteGame(gameId);
        return ResponseEntity.ok(Map.of("message", "Game " + gameId + " deleted"));
    }

    // -------------------- Scores / Leaderboard --------------------

    @GetMapping("/games/{gameId}/scores")
    public List<Map<String, Object>> getGameScores(@PathVariable Long gameId) {
        logger.info("Fetching scores for game: " + gameId);
        String sql = "SELECT s.id AS id, s.score_value AS score_value, s.timestamp AS timestamp, s.play_time AS play_time, " +
                "u.email AS email, u.first_name AS first_name, u.last_name AS last_name " +
                "FROM scores s JOIN users u ON s.user_id = u.id " +
                "WHERE s.game_id = ? " +
                "ORDER BY s.score_value DESC, s.timestamp DESC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, gameId.intValue());
        return rows.stream().map(this::normalizeRowToFrontend).collect(Collectors.toList());
    }

    @GetMapping("/games/{gameId}/leaderboard")
    public List<Map<String, Object>> getGameLeaderboard(@PathVariable Long gameId) {
        logger.info("Fetching leaderboard for game: " + gameId);
        String sql = "SELECT s.id AS id, s.user_id AS user_id, s.score_value AS score_value, s.timestamp AS timestamp, s.play_time AS play_time, " +
                "u.email AS email, u.first_name AS first_name, u.last_name AS last_name " +
                "FROM scores s JOIN users u ON s.user_id = u.id " +
                "WHERE s.game_id = ? " +
                "ORDER BY s.score_value DESC, s.timestamp ASC " +
                "LIMIT 10";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, gameId.intValue());
        return rows.stream().map(this::normalizeRowToFrontend).collect(Collectors.toList());
    }

    @DeleteMapping("/games/{gameId}/scores")
    public ResponseEntity<?> resetGameScores(@PathVariable Long gameId) {
        logger.info("Resetting scores for game: " + gameId);
        jdbcTemplate.update("DELETE FROM scores WHERE game_id = ?", gameId.intValue());
        return ResponseEntity.ok().body(Map.of("message", "All scores for game ID " + gameId + " have been reset"));
    }

    @DeleteMapping("/scores/{id}")
    public ResponseEntity<?> deleteScore(@PathVariable Integer id) {
        logger.info("Deleting score record: id=" + id);
        try {
            scoreRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Score " + id + " deleted"));
        } catch (Exception e) {
            logger.warning("Failed to delete score: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete score"));
        }
    }

    // -------------------- System Stats --------------------

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        logger.info("Fetching system statistics");
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalGames", gameRepository.count());
        stats.put("totalScores", scoreRepository.count());
        Number avgNum = jdbcTemplate.queryForObject(
                "SELECT COALESCE(AVG(score_value), 0) FROM scores", Number.class);
        stats.put("averageScore", avgNum == null ? 0 : avgNum.doubleValue());
        return stats;
    }

    // -------------------- Scores list endpoint used by admin UI --------------------

    /**
     * Return paged/filtered scores for admin UI.
     * - gameId optional
     * - email optional (partial match)
     *
     * Response:
     * {
     *   "scores": [ { id, email, gameName, scoreValue, playTime, timestamp }, ... ],
     *   "stats": { totalScores, uniqueUsers, averageScore, highestScore }
     * }
     */
    @GetMapping("/scores")
    public Map<String, Object> getScores(
            @RequestParam(required = false) Integer gameId,
            @RequestParam(required = false) String email) {

        logger.info("Fetching score records with filters - gameId: " + gameId + ", email: " + email);

        StringBuilder sql = new StringBuilder(
                "SELECT s.id AS id, s.user_id AS user_id, s.game_id AS game_id, s.score_value AS score_value, " +
                        "s.timestamp AS timestamp, s.play_time AS play_time, s.email AS email, g.name AS game_name " +
                        "FROM scores s LEFT JOIN games g ON s.game_id = g.id ");

        List<Object> params = new ArrayList<>();
        if (gameId != null || (email != null && !email.isEmpty())) {
            sql.append("WHERE ");
            boolean added = false;
            if (gameId != null) {
                sql.append("s.game_id = ? ");
                params.add(gameId);
                added = true;
            }
            if (email != null && !email.isEmpty()) {
                if (added) sql.append("AND ");
                sql.append("s.email LIKE ? ");
                params.add("%" + email + "%");
            }
        }
        sql.append("ORDER BY s.timestamp DESC");

        List<Map<String, Object>> rawScores;
        try {
            rawScores = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        } catch (Exception e) {
            logger.warning("SQL query for scores failed: " + e.getMessage() + " - falling back to JPA");
            rawScores = Collections.emptyList();
        }

        List<Map<String, Object>> scores;
        if (rawScores == null || rawScores.isEmpty()) {
            scores = getAllScoresFallback(gameId, email);
        } else {
            scores = rawScores.stream().map(this::normalizeRowToFrontend).collect(Collectors.toList());
        }

        Map<String, Object> stats = calculateStats(scores);

        Map<String, Object> result = new HashMap<>();
        result.put("scores", scores);
        result.put("stats", stats);
        return result;
    }

    // -------------------- Helpers --------------------

    // Normalize a row map from jdbcTemplate.queryForList to the frontend expected structure
    private Map<String, Object> normalizeRowToFrontend(Map<String, Object> row) {
        Map<String, Object> out = new HashMap<>();
        out.put("id", safeGet(row, "id"));
        out.put("email", safeGet(row, "email"));

        String gameName = asStringIgnoreCase(row, "game_name", "GAME_NAME", "name", "NAME");
        if (gameName == null || gameName.isEmpty()) {
            Object gidObj = getAnyKeyIgnoreCase(row, "game_id", "GAME_ID", "gameId", "GAMEID");
            Integer gid = toIntSafe(gidObj);
            gameName = (gid == 0) ? "Unknown" : gameService.getGameById(gid.longValue()).map(Game::getName).orElse("Unknown");
        }
        out.put("gameName", gameName);

        out.put("scoreValue", toIntSafe(getAnyKeyIgnoreCase(row, "score_value", "SCORE_VALUE", "scoreValue", "SCOREVALUE")));
        out.put("playTime", toIntSafe(getAnyKeyIgnoreCase(row, "play_time", "PLAY_TIME", "playTime", "PLAYTIME")));

        Object ts = getAnyKeyIgnoreCase(row, "timestamp", "TIMESTAMP", "time", "created_at");
        if (ts instanceof Timestamp) {
            out.put("timestamp", ((Timestamp) ts).toInstant().toString());
        } else if (ts != null) {
            out.put("timestamp", ts.toString());
        } else {
            out.put("timestamp", "");
        }

        return out;
    }

    // Try multiple candidate keys in the row map (case/format insensitive)
    private Object getAnyKeyIgnoreCase(Map<String, Object> row, String... candidates) {
        for (String k : candidates) {
            if (row.containsKey(k)) return row.get(k);
        }
        for (Map.Entry<String, Object> e : row.entrySet()) {
            for (String cand : candidates) {
                if (e.getKey().equalsIgnoreCase(cand)) return e.getValue();
            }
        }
        return null;
    }

    private String asStringIgnoreCase(Map<String, Object> row, String... candidates) {
        Object v = getAnyKeyIgnoreCase(row, candidates);
        return v == null ? "" : v.toString();
    }

    private Object safeGet(Map<String, Object> row, String key) {
        Object v = row.get(key);
        if (v != null) return v;
        for (Map.Entry<String, Object> e : row.entrySet()) {
            if (e.getKey().equalsIgnoreCase(key)) return e.getValue();
        }
        return null;
    }

    private int toIntSafe(Object o) {
        if (o == null) return 0;
        if (o instanceof Number) return ((Number) o).intValue();
        try { return Integer.parseInt(o.toString()); } catch (Exception e) { return 0; }
    }

    // Fallback using ScoreService + GameService (JPA) to avoid JDBC case/driver issues
    private List<Map<String, Object>> getAllScoresFallback(Integer gameId, String email) {
        logger.info("Falling back to JPA to fetch scores");
        List<Score> allScores = scoreService.getAllScores();
        Stream<Score> stream = allScores.stream();

        if (gameId != null) {
            stream = stream.filter(s -> s.getGameId() == gameId);
        }
        if (email != null && !email.isEmpty()) {
            String low = email.toLowerCase();
            stream = stream.filter(s -> s.getEmail() != null && s.getEmail().toLowerCase().contains(low));
        }

        return stream
                .sorted(Comparator.comparing(Score::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(s -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", s.getId());
                    m.put("email", s.getEmail());
                    m.put("gameName", gameService.getGameById((long) s.getGameId()).map(Game::getName).orElse("Unknown"));
                    m.put("scoreValue", s.getScoreValue());
                    m.put("playTime", s.getPlayTime());
                    m.put("timestamp", s.getTimestamp() != null ? s.getTimestamp().toInstant().toString() : "");
                    return m;
                })
                .collect(Collectors.toList());
    }

    // Calculate statistics (fields expected by frontend)
    private Map<String, Object> calculateStats(List<Map<String, Object>> scores) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalScores", scores.size());
        long uniqueUsers = scores.stream()
                .map(s -> Optional.ofNullable(s.get("email")).map(Object::toString).orElse(""))
                .filter(e -> !e.isEmpty())
                .distinct()
                .count();
        stats.put("uniqueUsers", uniqueUsers);

        if (!scores.isEmpty()) {
            double avg = scores.stream()
                    .mapToInt(s -> toIntSafe(s.get("scoreValue")))
                    .average()
                    .orElse(0.0);
            stats.put("averageScore", Math.round(avg * 100.0) / 100.0);

            int max = scores.stream()
                    .mapToInt(s -> toIntSafe(s.get("scoreValue")))
                    .max()
                    .orElse(0);
            stats.put("highestScore", max);
        } else {
            stats.put("averageScore", 0);
            stats.put("highestScore", 0);
        }
        return stats;
    }
}
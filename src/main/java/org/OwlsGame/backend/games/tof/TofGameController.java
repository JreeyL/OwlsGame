package org.OwlsGame.backend.games.tof;

import org.OwlsGame.backend.models.Game;
import org.OwlsGame.backend.models.Score;
import org.OwlsGame.backend.models.Session;
import org.OwlsGame.backend.models.User;
import org.OwlsGame.backend.service.GameService;
import org.OwlsGame.backend.service.ScoreService;
import org.OwlsGame.backend.service.SessionService;
import org.OwlsGame.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/tof")
public class TofGameController {

    private static final Logger logger = Logger.getLogger(TofGameController.class.getName());

    @Autowired
    private TofGameService tofGameService;

    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private SessionService sessionService;

    @GetMapping("/questions")
    public List<TofQuestion> getQuestions(@RequestParam(defaultValue = "10") int num) {
        logger.info("Fetching " + num + " TOF questions");

        // 从数据库获取问题
        List<TofQuestion> questions = tofGameService.getRandomQuestions(num);

        // 如果数据库中没有问题，提供默认问题
        if (questions == null || questions.isEmpty()) {
            logger.info("No questions found in database, providing default questions");

            List<TofQuestion> defaultQuestions = new ArrayList<>();
            defaultQuestions.add(createDefaultQuestion(1L, "Humans share 50% of their DNA with bananas.", true));
            defaultQuestions.add(createDefaultQuestion(2L, "The Earth is the only planet in our solar system that has a moon.", false));
            defaultQuestions.add(createDefaultQuestion(3L, "Goldfish have a memory span of only three seconds.", false));
            defaultQuestions.add(createDefaultQuestion(4L, "The Eiffel Tower can be 15 cm taller during the summer.", true));
            defaultQuestions.add(createDefaultQuestion(5L, "Honey never spoils.", true));

            // 只返回请求的问题数量
            int returnSize = Math.min(num, defaultQuestions.size());
            return defaultQuestions.subList(0, returnSize);
        }

        return questions;
    }

    /**
     * 创建带有默认ID的问题，用于前端显示
     */
    private TofQuestion createDefaultQuestion(Long id, String content, boolean answer) {
        TofQuestion q = new TofQuestion();
        q.setId(id);           // 设置ID以便前端识别
        q.setContent(content);
        q.setAnswer(answer);
        return q;
    }

    @PostMapping("/check")
    public boolean checkAnswer(@RequestBody Map<String, Object> payload) {
        Long questionId = Long.valueOf(payload.get("questionId").toString());
        boolean answer = Boolean.parseBoolean(payload.get("answer").toString());
        logger.info("Checking answer for question ID " + questionId + ": " + answer);

        // 对于默认问题的特殊处理
        if (questionId <= 5) {
            // 硬编码的默认问题答案
            boolean[] defaultAnswers = {true, false, false, true, true};
            if (questionId >= 1 && questionId <= 5) {
                boolean correctAnswer = defaultAnswers[questionId.intValue() - 1];
                return answer == correctAnswer;
            }
        }

        // 常规数据库问题检查
        return tofGameService.checkAnswer(questionId, answer);
    }

    @PostMapping("/score")
    public Map<String, Object> saveScore(@RequestBody Map<String, Object> payload, HttpSession httpSession) {
        try {
            logger.info("Received score data: " + payload);

            String email = payload.get("username").toString();
            int scoreValue = Integer.parseInt(payload.get("score").toString());
            int playTime = payload.containsKey("playTime") ?
                    Integer.parseInt(payload.get("playTime").toString()) : 60; // 默认60秒

            logger.info("Processing score: " + scoreValue + " for user " + email + " (play time: " + playTime + "s)");

            // 1. 查找用户 - 多种方式尝试
            User user = null;

            // 1.1 通过邮箱查找
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isPresent()) {
                user = userOpt.get();
                logger.info("Found user by email: " + user.getEmail() + " (ID: " + user.getId() + ")");
            } else {
                logger.warning("User not found with email: " + email + ", trying session...");

                // 1.2 从会话中获取用户
                User sessionUser = (User) httpSession.getAttribute("user");
                if (sessionUser != null) {
                    user = sessionUser;
                    // 更新邮箱以确保一致性
                    email = sessionUser.getEmail();
                    logger.info("Found user from session: " + email + " (ID: " + user.getId() + ")");
                } else {
                    // 1.3 尝试从数据库中找到任何用户（临时解决方案）
                    logger.warning("No user in session, looking for any user with this email");

                    // 这种情况不应该发生，返回错误
                    logger.severe("Failed to find user for saving score");
                    return Map.of(
                            "success", false,
                            "error", "User not found. Please login again.",
                            "userEmail", email
                    );
                }
            }

            // 2. 查找或创建TOF游戏
            Game game = null;
            Optional<Game> gameOpt = gameService.findByName("True/False");

            if (gameOpt.isPresent()) {
                game = gameOpt.get();
                logger.info("Found existing game: " + game.getName() + " (ID: " + game.getId() + ")");
            } else {
                // 创建新游戏
                Game newGame = new Game("True/False", 1000); // 最高分1000
                game = gameService.createGame(newGame);
                logger.info("Created new game: " + game.getName() + " (ID: " + game.getId() + ")");
            }

            // 3. 创建分数记录
            Score score = new Score(
                    (int)user.getId(),        // userId
                    game.getId().intValue(),  // gameId
                    scoreValue,               // scoreValue
                    Timestamp.from(Instant.now()), // timestamp
                    email,                    // email
                    playTime                  // playTime in seconds
            );

            // 4. 保存分数
            Score savedScore = scoreService.saveScore(score);
            logger.info("Saved score with ID: " + savedScore.getId());

            // 5. 更新会话累计分数
            try {
                String sessionId = httpSession.getId();
                Optional<Session> dbSession = sessionService.findBySessionId(sessionId);
                if (dbSession.isPresent()) {
                    Session userSession = dbSession.get();
                    sessionService.updateCumulativeScore(userSession.getId(), scoreValue);
                    logger.info("Updated cumulative score for session: " + userSession.getId());
                } else {
                    logger.warning("No session found for ID: " + sessionId + ", cumulative score not updated");
                }
            } catch (Exception e) {
                logger.warning("Error updating cumulative score: " + e.getMessage());
                // 不影响主流程，继续执行
            }

            // 6. 同时保存到旧系统以保持兼容性
            try {
                tofGameService.saveScore(email, scoreValue);
                logger.info("Also saved to legacy tof_scores table");
            } catch (Exception e) {
                logger.warning("Error saving to legacy table: " + e.getMessage());
                // 不影响主流程，继续执行
            }

            logger.info("Score saved successfully for " + email);
            return Map.of(
                    "success", true,
                    "scoreId", savedScore.getId(),
                    "message", "Score saved successfully"
            );

        } catch (Exception e) {
            logger.severe("Error saving score: " + e.getMessage());
            e.printStackTrace();
            return Map.of(
                    "success", false,
                    "error", e.getMessage(),
                    "errorType", e.getClass().getSimpleName()
            );
        }
    }
}
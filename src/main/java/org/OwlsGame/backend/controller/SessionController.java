package org.OwlsGame.backend.controller;

import org.OwlsGame.backend.models.Session;
import org.OwlsGame.backend.models.User;
import org.OwlsGame.backend.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    private static final Logger logger = Logger.getLogger(SessionController.class.getName());

    @Autowired
    private SessionService sessionService;

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentSession(HttpSession httpSession) {
        String sessionId = httpSession.getId();
        logger.info("Getting session information for ID: " + sessionId);

        Optional<Session> sessionOpt = sessionService.findBySessionId(sessionId);
        Map<String, Object> response = new HashMap<>();

        // 获取 HttpSession 中的用户
        User user = (User) httpSession.getAttribute("user");
        boolean isLoggedIn = user != null;

        // 明确添加登录状态标志
        response.put("isLoggedIn", isLoggedIn);

        if (sessionOpt.isPresent()) {
            Session session = sessionOpt.get();
            logger.info("Found session for ID: " + sessionId + ", user ID: " + session.getUserId());

            // 添加会话数据
            response.put("id", session.getId());
            response.put("sessionId", session.getSessionId());
            response.put("userId", session.getUserId());
            response.put("creationTime", session.getCreationTime());
            response.put("lastAccessedTime", session.getLastAccessedTime());
            response.put("lastPlayedGameId", session.getLastPlayedGameId());
            response.put("favoriteGameId", session.getFavoriteGameId());
            response.put("cumulativeScore", session.getCumulativeScore());

            // 添加用户信息（如果存在）
            if (isLoggedIn) {
                logger.info("User is logged in: " + user.getEmail());
                response.put("email", user.getEmail());
                response.put("firstname", user.getFirstname());
                response.put("lastname", user.getLastname());
                response.put("userId", user.getId());
            }
        } else {
            logger.warning("No session found for ID: " + sessionId);
            response.put("error", "Session not found");

            // 仍然添加用户信息（如果存在）
            if (isLoggedIn) {
                logger.info("User in HttpSession but no DB session: " + user.getEmail());
                response.put("email", user.getEmail());
                response.put("firstname", user.getFirstname());
                response.put("lastname", user.getLastname());
                response.put("userId", user.getId());
            }
        }

        return ResponseEntity.ok(response);
    }
}
package org.OwlsGame.backend.games.tof;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tof")
public class TofGameController {

    @Autowired
    private TofGameService tofGameService;

    @GetMapping("/questions")
    public List<TofQuestion> getQuestions(@RequestParam(defaultValue = "10") int num) {
        return tofGameService.getRandomQuestions(num);
    }

    @PostMapping("/check")
    public boolean checkAnswer(@RequestBody Map<String, Object> payload) {
        Long questionId = Long.valueOf(payload.get("questionId").toString());
        boolean answer = Boolean.parseBoolean(payload.get("answer").toString());
        return tofGameService.checkAnswer(questionId, answer);
    }

    @PostMapping("/score")
    public void saveScore(@RequestBody Map<String, Object> payload) {
        String username = payload.get("username").toString();
        int score = Integer.parseInt(payload.get("score").toString());
        tofGameService.saveScore(username, score);
    }
}
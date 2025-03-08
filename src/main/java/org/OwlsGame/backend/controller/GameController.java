//package org.OwlsGame.backend.controller;
//
//import org.OwlsGame.backend.models.Game;
//import org.OwlsGame.backend.models.Score;
//import org.OwlsGame.backend.service.GameService;
//import org.OwlsGame.backend.service.ScoreService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpSession;
//import java.util.Date;
//
//@Controller
//@RequestMapping("/games")
//public class GameController {
//
//    @Autowired
//    private GameService gameService;
//
//    @Autowired
//    private ScoreService scoreService;
//
//    @GetMapping("/{id}")
//    public String playGame(@PathVariable int id, HttpSession session, Model model) {
//        model.addAttribute("gameId", id);
//        session.setAttribute("gameStartTime", System.currentTimeMillis());
//        return "playGame";
//    }
//
//    @PostMapping("/end")
//    public String endGame(@RequestParam int score, @RequestParam int playTime, @RequestParam int gameId, HttpSession session, Model model) {
//        String email = (String) session.getAttribute("email");
//        if (email != null) {
//            // 保存分数到数据库
//            saveScoreToDatabase(email, score, playTime, gameId);
//        }
//
//        model.addAttribute("score", score);
//        model.addAttribute("playTime", playTime);
//        return "endGame";
//    }
//
//    private void saveScoreToDatabase(String email, int score, int playTime, int gameId) {
//        Score scoreRecord = new Score();
//        scoreRecord.setEmail(email);
//        scoreRecord.setScoreValue(score);
//        scoreRecord.setPlayTime(playTime);
//        scoreRecord.setGameId(gameId);
//        scoreService.saveScore(scoreRecord);
//    }
//}
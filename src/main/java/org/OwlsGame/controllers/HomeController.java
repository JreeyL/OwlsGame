package org.OwlsGame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String showHome() {
        // 明确指定扩展名
        return "Homepage.jsp"; // 或 "Homepage.html"
    }

    @GetMapping("/TofGame")
    public String showTofGame() {
        return "TofGame.jsp"; // 或 "TofGame.html"
    }

    @GetMapping("/leaderboard")
    public String showLeaderboard() {
        return "leaderboard.jsp"; // 或 "leaderboard.html"
    }
}
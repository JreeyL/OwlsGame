package org.OwlsGame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/game-admin")
public class GameAdminController {

    @GetMapping("/games")
    public String showGamesAdmin() {
        return "forward:/admin/games.html";
    }

    @GetMapping("/games-scores")
    public String showGamesScores() {
        return "forward:/admin/games-scores.html";
    }
}
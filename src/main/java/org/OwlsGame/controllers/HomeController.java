package org.OwlsGame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String showHome() {
        return "Homepage";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "loginPage";
    }

    // 不要再有 @GetMapping("/usersRegister")，否则会冲突！

    @GetMapping("/TofGame")
    public String showTofGame() {
        return "TofGame";
    }

    @GetMapping("/leaderboard")
    public String showLeaderboard() {
        return "leaderboard";
    }
}
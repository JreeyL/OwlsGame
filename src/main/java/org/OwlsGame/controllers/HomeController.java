package org.OwlsGame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String showHome() {
        // 返回视图名称，不包含扩展名
        return "Homepage";
    }

    @GetMapping("/leaderboard")
    public String showLeaderboard() {
        return "leaderboard";
    }
}
package org.OwlsGame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "loginPage"; // 对应 /WEB-INF/views/loginPage.jsp
    }

    @GetMapping("/home")
    public String showHomePage() {
        return "loginPage"; // 对应 /WEB-INF/views/loginPage.jsp
    }
}
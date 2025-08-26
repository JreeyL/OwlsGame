package org.OwlsGame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Debug controller is working!";
    }

    @GetMapping("/admin-page")
    public String adminPage() {
        // 直接返回静态HTML页面路径，不进行重定向
        return "forward:/admin/test-page.html";
    }
}
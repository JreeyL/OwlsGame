package org.OwlsGame.backend.controller;

import org.OwlsGame.backend.dto.UserRegisterDto;
import org.OwlsGame.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // GET - 展示注册页面
    @GetMapping("/usersRegister")
    public String showRegisterPage(Model model) {
        model.addAttribute("userRegisterDto", new UserRegisterDto());
        return "usersRegister";
    }

    // POST - 处理注册
    @PostMapping("/register")
    public String register(@ModelAttribute("userRegisterDto") UserRegisterDto userRegisterDto, Model model) {
        try {
            userService.registerUser(userRegisterDto);
            // 注册成功后采用重定向，防止表单重复提交
            return "redirect:/userDetails";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "usersRegister";
        }
    }

    // GET - 展示注册成功页面
    @GetMapping("/userDetails")
    public String showUserDetailsPage() {
        return "userDetails";
    }
}
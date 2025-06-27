package org.OwlsGame.backend.controller;

import org.OwlsGame.backend.models.User;
import org.OwlsGame.backend.service.OtpService;
import org.OwlsGame.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;
import java.util.logging.Logger;

@Controller
public class PasswordResetController {

    private static final Logger logger = Logger.getLogger(PasswordResetController.class.getName());

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    // 修改为重定向到静态资源目录
    @GetMapping("/reset-password")
    public String showResetPwdPage() {
        logger.info("重置密码页面请求 - 重定向到静态资源");
        return "redirect:/static/ResetPwd.html";
    }

    // 新增直接访问 NewPwd 的处理方法
    @GetMapping("/NewPwd.html")
    public String showNewPwdPage() {
        logger.info("直接访问 NewPwd.html - 重定向到静态资源");
        return "redirect:/static/NewPwd.html";
    }

    // 处理重置密码邮箱提交
    @PostMapping("/submit-reset")
    public String submitReset(@RequestParam("email") String email) {
        try {
            // 检查邮箱是否存在
            Optional<User> userOptional = userService.getUserByEmail(email);
            if (userOptional.isEmpty()) {
                return "redirect:/static/ResetPwd.html?error=Email not found in our records!";
            }

            // 生成并发送OTP
            otpService.createAndSendOtp(email);

            // 使用URL参数传递email和成功消息
            String redirectUrl = UriComponentsBuilder.fromPath("/static/NewPwd.html")
                    .queryParam("email", email)
                    .queryParam("message", "OTP code sent to your email!")
                    .build().toUriString();

            return "redirect:" + redirectUrl;
        } catch (Exception e) {
            logger.severe("Error in submit-reset: " + e.getMessage());
            return "redirect:/static/ResetPwd.html?error=" + e.getMessage();
        }
    }

    // 处理新密码提交
    @PostMapping("/submit-new-pwd")
    public String submitNewPwd(@RequestParam("email") String email,
                               @RequestParam("otpcode") String otp,
                               @RequestParam("password") String password,
                               @RequestParam("rep_password") String repPassword) {
        try {
            // 验证两次输入的密码是否一致
            if (!password.equals(repPassword)) {
                return "redirect:/static/NewPwd.html?email=" + email + "&error=Passwords do not match!";
            }

            // 验证OTP
            boolean isValidOtp = otpService.validateOtp(email, otp);
            if (!isValidOtp) {
                return "redirect:/static/NewPwd.html?email=" + email + "&error=Invalid or expired OTP code!";
            }

            // 重置密码
            boolean resetSuccess = userService.resetPassword(email, password);
            if (!resetSuccess) {
                return "redirect:/static/NewPwd.html?email=" + email + "&error=Failed to reset password!";
            }

            // 密码重置成功，重定向到登录页面
            return "redirect:/login?resetSuccess=Password has been reset successfully!";
        } catch (Exception e) {
            logger.severe("Error in submit-new-pwd: " + e.getMessage());
            return "redirect:/static/NewPwd.html?email=" + email + "&error=" + e.getMessage();
        }
    }
}
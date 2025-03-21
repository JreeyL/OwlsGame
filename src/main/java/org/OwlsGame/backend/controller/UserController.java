//package org.OwlsGame.backend.controller;
//
//import org.OwlsGame.backend.models.User;
//import org.OwlsGame.backend.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import jakarta.servlet.http.HttpSession;
//
//@Controller
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/login")
//    public String showLoginPage() {
//        return "LoginPage";
//    }
//
//    @PostMapping("/login")
//    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
//        if (userService.isAccountLocked(username)) {
//            model.addAttribute("message", "Account is locked. Please try again later.");
//            return "LoginPage";
//        }
//
//        if (userService.validateUser(username, password)) {
//            session.setAttribute("attempts", 5);
//            session.setAttribute("username", username);
//
//            //session.setAttribute("email", userService.getUserByUsername(username).getEmail());
//
//            // Initialize user tracking data in the session
//            //session.setAttribute("lastPlayedGame", null);
//            //session.setAttribute("favoriteGame", null);
//            //session.setAttribute("cumulativeScore", 0);
//
//            model.addAttribute("loginSuccess", true);
//            return "LoginPage";
//        } else {
//            int attempts = (session.getAttribute("attempts") != null) ? (int) session.getAttribute("attempts") : 5;
//            attempts--;
//            session.setAttribute("attempts", attempts);
//            if (attempts > 0) {
//                model.addAttribute("message", "Invalid username or password. You have " + attempts + " attempts left.");
//            } else {
//                model.addAttribute("message", "Too many failed attempts. Please try again later.");
//            }
//            return "LoginPage";
//        }
//    }
//
//    @GetMapping("/register")
//    public String showRegistrationPage() {
//        return "usersRegister";
//    }
//
//    @PostMapping("/register")
//    public String register(@RequestParam String firstname, @RequestParam String lastname,
//                           @RequestParam String email, @RequestParam String password, Model model) {
//        User user = new User();
//        user.setFirstname(firstname);
//        user.setLastname(lastname);
//        user.setEmail(email);
//        user.setPassword(password);
//
//        try {
//            userService.createUser(user);
//            return "redirect:/login";
//        } catch (Exception e) {
//            model.addAttribute("error", "Error registering user: " + e.getMessage());
//            return "usersRegister";
//        }
//    }
//
//    @GetMapping("/reset-password")
//    public String showResetPasswordPage() {
//        return "RecoveryPwd";
//    }
//
//    @PostMapping("/reset-password-request")
//    public String resetPasswordRequest(@RequestParam String email, HttpSession session, Model model) {
//        User user = userService.getUserByEmail(email);
//        if (user != null) {
//            session.setAttribute("resetEmail", email);
//            return "redirect:/new-password";
//        } else {
//            model.addAttribute("error", "User not found with email: " + email);
//            return "RecoveryPwd";
//        }
//    }
//
//    @GetMapping("/new-password")
//    public String showNewPasswordPage() {
//        return "NewPwd";
//    }
//
//    @PostMapping("/new-password")
//    public String updatePassword(@RequestParam String newPassword, HttpSession session, Model model) {
//        String email = (String) session.getAttribute("resetEmail");
//        if (email != null) {
//            User user = userService.getUserByEmail(email);
//            if (user != null) {
//                user.setPassword(newPassword);
//                userService.updateUser(user);
//                session.removeAttribute("resetEmail");
//                return "redirect:/login";
//            } else {
//                model.addAttribute("error", "User not found with email: " + email);
//                return "NewPwd";
//            }
//        } else {
//            model.addAttribute("error", "Session expired. Please try again.");
//            return "RecoveryPwd";
//        }
//    }
//}
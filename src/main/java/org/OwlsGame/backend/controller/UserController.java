package org.OwlsGame.backend.controller;

import org.OwlsGame.backend.dto.UserLoginDto;
import org.OwlsGame.backend.dto.UserRegisterDto;
import org.OwlsGame.backend.models.Session;
import org.OwlsGame.backend.models.User;
import org.OwlsGame.backend.service.SessionService;
import org.OwlsGame.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.Instant;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    // GET - Display registration page
    @GetMapping("/usersRegister")
    public String showRegisterPage(Model model) {
        model.addAttribute("userRegisterDto", new UserRegisterDto());
        return "usersRegister";
    }

    // POST - Handle registration
    @PostMapping("/register")
    public String register(@ModelAttribute("userRegisterDto") UserRegisterDto userRegisterDto, Model model) {
        try {
            userService.registerUser(userRegisterDto);
            // Redirect after successful registration to prevent form resubmission
            return "redirect:/userDetails";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "usersRegister";
        }
    }

    // GET - Show registration success page
    @GetMapping("/userDetails")
    public String showUserDetailsPage() {
        return "userDetails";
    }

    // -------------------- Login Section --------------------

    // GET - Display login page
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("userLoginDto", new UserLoginDto());
        model.addAttribute("message", null);
        model.addAttribute("messageType", null);
        return "loginPage";
    }

    // POST - Handle login
    @PostMapping("/login")
    public String login(@ModelAttribute("userLoginDto") UserLoginDto userLoginDto,
                        Model model,
                        HttpSession httpSession) {
        String email = userLoginDto.getEmail();
        String password = userLoginDto.getPassword();
        int maxAttempts = 3;

        User user = userService.getUserByEmail(email).orElse(null);

        if (user == null) {
            // No such user
            model.addAttribute("userLoginDto", userLoginDto);
            model.addAttribute("message", "Incorrect email or password. You have " + maxAttempts + " attempts remaining.");
            model.addAttribute("messageType", "failure");
            return "loginPage";
        }

        // Check if the account is locked (auto-unlock handled in service)
        if (userService.isAccountLocked(user)) {
            model.addAttribute("userLoginDto", userLoginDto);
            model.addAttribute("message", "Your account has been locked. Please try again after 5 minutes.");
            model.addAttribute("messageType", "failure");
            return "loginPage";
        }

        if (userService.validateCredentials(email, password)) {
            // Login successful, store user in session
            httpSession.setAttribute("user", user);

            // Create custom session record
            String sessionId = httpSession.getId();
            Timestamp now = Timestamp.from(Instant.now());
            Session session = new Session(sessionId, user.getId(), now);
            sessionService.createSession(session);

            // Redirect to homepage after successful login
            return "redirect:/homepage";
        } else {
            // Refresh user object from database to get updated attempts/locked status
            user = userService.getUserByEmail(email).orElse(user);
            int leftAttempts = maxAttempts - user.getLoginAttempts();

            model.addAttribute("userLoginDto", userLoginDto);

            // If now locked, show locked message
            if (userService.isAccountLocked(user)) {
                model.addAttribute("message", "Your account has been locked. Please try again after 5 minutes.");
            } else {
                model.addAttribute("message", "Incorrect email or password. You have " + leftAttempts + " attempts remaining.");
            }
            model.addAttribute("messageType", "failure");
            return "loginPage";
        }
    }

    // GET - Homepage (requires login)
    @GetMapping("/homepage")
    public String homepage(HttpSession httpSession, Model model) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            // Not logged in, redirect to login page
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "Homepage"; // Must match JSP filename exactly
    }

    // GET - Logout
    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.invalidate();
        return "redirect:/login";
    }
}
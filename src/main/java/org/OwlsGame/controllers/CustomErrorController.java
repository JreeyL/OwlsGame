package org.OwlsGame.controllers;

import jakarta.servlet.RequestDispatcher;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = Logger.getLogger(CustomErrorController.class.getName());

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            logger.warning("错误状态码: " + statusCode);

            if(statusCode == 404) {
                return "redirect:/static/error/404.html";
            }
        }
        return "redirect:/static/error/500.html";
    }
}
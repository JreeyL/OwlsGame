package org.OwlsGame.backend;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(HelloServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException { // 移除IOException
        resp.setContentType("text/plain");
        try {
            PrintWriter writer = resp.getWriter();
            writer.println("Hello from Spring Boot Servlet!");
        } catch (IOException e) {
            // 记录异常
            logger.log(Level.SEVERE, "Unable to get response writer", e);
            // 设置错误状态
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
package org.OwlsGame.backend;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;

public class AdminAuthFilter implements Filter {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "OwlsGameAdmin2025!";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestURI = httpRequest.getRequestURI();

        // 只过滤/admin/路径下的请求
        if (requestURI.startsWith("/admin/")) {
            // 检查会话中是否有管理员标志
            Boolean isAdmin = session != null ? (Boolean) session.getAttribute("isAdmin") : null;

            if (isAdmin != null && isAdmin) {
                // 已认证，放行请求
                chain.doFilter(request, response);
                return;
            }

            // 检查Basic认证
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Basic ")) {
                String base64Credentials = authHeader.substring("Basic ".length());
                String credentials = new String(Base64.getDecoder().decode(base64Credentials));
                String[] values = credentials.split(":", 2);

                if (values.length == 2 && ADMIN_USERNAME.equals(values[0]) &&
                        ADMIN_PASSWORD.equals(values[1])) {

                    // 认证成功，设置会话标志
                    if (session == null) {
                        session = httpRequest.getSession(true);
                    }
                    session.setAttribute("isAdmin", true);

                    // 放行请求
                    chain.doFilter(request, response);
                    return;
                }
            }

            // 未认证，发送401响应
            httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"Admin Area\"");
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Admin authentication required");
        } else {
            // 非管理区域请求，直接放行
            chain.doFilter(request, response);
        }
    }
}
package org.OwlsGame.backend;

import jakarta.servlet.*;
import java.io.IOException;

public class LoggingFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("LoggingFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("Request received: " +
                request.getRemoteAddr() + " @ " + System.currentTimeMillis());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("LoggingFilter destroyed");
    }
}
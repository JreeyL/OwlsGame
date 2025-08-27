package org.OwlsGame.backend;

import jakarta.servlet.ServletContextEvent;  // 关键修改：使用jakarta包
import jakarta.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== Application Started ===");
        System.out.println("Server Info: " + sce.getServletContext().getServerInfo());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== Application Shutdown ===");
    }
}
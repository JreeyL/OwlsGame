package org.OwlsGame.config;

import org.OwlsGame.backend.AppContextListener;
import org.OwlsGame.backend.HelloServlet;
import org.OwlsGame.backend.LoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    // 注册Servlet（使用Jakarta EE 9+规范）
    @Bean
    public ServletRegistrationBean<HelloServlet> helloServlet() {
        return new ServletRegistrationBean<>(
                new HelloServlet(),
                "/hello"
        );
    }

    // 注册Filter（适配Jakarta）
    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LoggingFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    // 注册Listener（适配Jakarta）
    @Bean
    public ServletListenerRegistrationBean<AppContextListener> appListener() {
        return new ServletListenerRegistrationBean<>(new AppContextListener());
    }
}
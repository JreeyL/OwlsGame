package org.OwlsGame.config;

import org.OwlsGame.backend.AppContextListener;
import org.OwlsGame.backend.HelloServlet;
import org.OwlsGame.backend.LoggingFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = WebConfig.class)
class WebConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void testHelloServletRegistration() {
        // 获取ServletRegistrationBean
        ServletRegistrationBean<HelloServlet> registrationBean =
                context.getBean("helloServlet", ServletRegistrationBean.class);

        // 验证Bean不为null
        assertNotNull(registrationBean, "Servlet registration bean should not be null");

        // 验证注册的Servlet类型
        assertTrue(registrationBean.getServlet() instanceof HelloServlet,
                "Registration should be for HelloServlet");

        // 验证URL映射
        Collection<String> urlMappings = registrationBean.getUrlMappings();
        assertEquals(1, urlMappings.size(), "Should have exactly one URL mapping");
        assertTrue(urlMappings.contains("/hello"), "URL mapping should be /hello");
    }

    @Test
    void testLoggingFilterRegistration() {
        // 获取FilterRegistrationBean
        FilterRegistrationBean<LoggingFilter> registrationBean =
                context.getBean("loggingFilter", FilterRegistrationBean.class);

        // 验证Bean不为null
        assertNotNull(registrationBean, "Filter registration bean should not be null");

        // 验证注册的Filter类型
        assertTrue(registrationBean.getFilter() instanceof LoggingFilter,
                "Registration should be for LoggingFilter");

        // 验证URL模式
        Collection<String> urlPatterns = registrationBean.getUrlPatterns();
        assertEquals(1, urlPatterns.size(), "Should have exactly one URL pattern");
        assertTrue(urlPatterns.contains("/*"), "URL pattern should be /*");
    }

    @Test
    void testAppListenerRegistration() {
        // 获取ServletListenerRegistrationBean
        ServletListenerRegistrationBean<AppContextListener> registrationBean =
                context.getBean("appListener", ServletListenerRegistrationBean.class);

        // 验证Bean不为null
        assertNotNull(registrationBean, "Listener registration bean should not be null");

        // 验证注册的Listener类型
        assertTrue(registrationBean.getListener() instanceof AppContextListener,
                "Registration should be for AppContextListener");
    }
}
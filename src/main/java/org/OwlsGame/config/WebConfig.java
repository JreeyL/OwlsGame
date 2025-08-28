package org.OwlsGame.config;

import org.OwlsGame.backend.AdminAuthFilter;
import org.OwlsGame.backend.AppContextListener;
import org.OwlsGame.backend.HelloServlet;
import org.OwlsGame.backend.LoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

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

    // 为JSP文件配置ViewResolver
    @Bean
    public ViewResolver jspViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        resolver.setOrder(0); // 优先级较高
        return resolver;
    }

    // 为HTML文件配置专用ViewResolver
    @Bean
    public ViewResolver htmlViewResolver() {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".html");
        resolver.setViewClass(InternalResourceView.class); // 使用InternalResourceView
        resolver.setOrder(1); // 优先级较低
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加静态资源处理
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/", "/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/", "/js/");
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/", "/images/");

        // 访问WEB-INF之外的页面文件
        registry.addResourceHandler("/*.html").addResourceLocations("classpath:/static/");

        // 其他静态资源
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(htmlViewResolver());
        registry.viewResolver(jspViewResolver());
    }

    @Bean
    public FilterRegistrationBean<AdminAuthFilter> adminAuthFilter() {
        FilterRegistrationBean<AdminAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AdminAuthFilter());
        registration.addUrlPatterns("/admin/*");
        registration.setOrder(1); // 高优先级
        return registration;
    }

}
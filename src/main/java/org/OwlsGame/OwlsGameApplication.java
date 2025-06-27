package org.OwlsGame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"org.OwlsGame", "org.OwlsGame.controllers", "org.OwlsGame.backend", "org.OwlsGame.config"})
@EntityScan(basePackages = "org.OwlsGame.backend.models")  // 明确扫描实体类
@EnableJpaRepositories(basePackages = "org.OwlsGame.backend.dao")  // 明确扫描Repository接口
public class OwlsGameApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(OwlsGameApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(OwlsGameApplication.class, args);
    }
}
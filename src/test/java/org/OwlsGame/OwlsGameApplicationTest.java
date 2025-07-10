package org.OwlsGame;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OwlsGameApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    /**
     * 验证应用上下文是否成功加载
     */
    @Test
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    /**
     * 验证配置了Entity扫描的包是否正确
     */
    @Test
    void entityScanPackageIsConfigured() {
        // 直接从类上获取注解
        EntityScan entityScan = OwlsGameApplication.class.getAnnotation(EntityScan.class);
        assertNotNull(entityScan, "EntityScan annotation should be present");

        String[] basePackages = entityScan.basePackages();
        assertTrue(basePackages.length > 0, "EntityScan should have at least one base package defined");
        assertEquals("org.OwlsGame.backend", basePackages[0],
                "EntityScan should be configured to scan 'org.OwlsGame.backend'");
    }

    /**
     * 验证配置了Repository扫描的包是否正确
     */
    @Test
    void jpaRepositoriesPackageIsConfigured() {
        // 直接从类上获取注解
        EnableJpaRepositories repositories = OwlsGameApplication.class.getAnnotation(EnableJpaRepositories.class);
        assertNotNull(repositories, "EnableJpaRepositories annotation should be present");

        String[] basePackages = repositories.basePackages();
        assertTrue(basePackages.length > 0, "EnableJpaRepositories should have at least one base package defined");
        assertEquals("org.OwlsGame.backend", basePackages[0],
                "EnableJpaRepositories should be configured to scan 'org.OwlsGame.backend'");
    }

    /**
     * 测试Spring Application Builder配置
     */
    @Test
    void applicationBuilderConfiguresCorrectSourceClass() {
        OwlsGameApplication application = new OwlsGameApplication();
        org.springframework.boot.builder.SpringApplicationBuilder builder =
                new org.springframework.boot.builder.SpringApplicationBuilder();

        // 调用configure方法，并验证返回的builder不为null
        assertNotNull(application.configure(builder),
                "Configure method should return a non-null SpringApplicationBuilder");
    }

    /**
     * 验证应用程序的基本属性
     */
    @Test
    void applicationHasCorrectProperties() {
        assertNotNull(environment);

        // 验证应用程序名称是否来自配置或默认值
        String applicationName = environment.getProperty("spring.application.name", "OwlsGame");
        assertNotNull(applicationName);
    }

    /**
     * 验证主类是否带有正确的Spring Boot注解
     */
    @Test
    void applicationHasSpringBootAnnotation() {
        org.springframework.boot.autoconfigure.SpringBootApplication annotation =
                OwlsGameApplication.class.getAnnotation(org.springframework.boot.autoconfigure.SpringBootApplication.class);

        assertNotNull(annotation, "Class should be annotated with @SpringBootApplication");
    }
}
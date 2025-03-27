package org.OwlsGame.backend;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class AppContextListenerTest {

    private AppContextListener listener;

    // 只模拟ServletContext
    @Mock
    private ServletContext mockContext;

    // ServletContextEvent将使用真实实例
    private ServletContextEvent event;

    // For capturing System.out
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        // 初始化Mockito注解
        MockitoAnnotations.openMocks(this);

        // 创建监听器实例
        listener = new AppContextListener();

        // 配置模拟行为
        when(mockContext.getServerInfo()).thenReturn("Test Server");

        // 使用模拟的ServletContext创建真实的ServletContextEvent
        event = new ServletContextEvent(mockContext);

        // 重定向System.out以捕获输出
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        // 恢复原始System.out
        System.setOut(originalOut);
    }

    @Test
    public void testContextInitialized() {
        // 调用测试方法
        listener.contextInitialized(event);

        // 获取捕获的输出
        String output = outputStream.toString();

        // 验证输出包含预期消息
        assertTrue(output.contains("=== 应用启动 ==="), "Should log application startup");
        assertTrue(output.contains("Server Info: Test Server"), "Should log server info");
    }

    @Test
    public void testContextDestroyed() {
        // 调用测试方法
        listener.contextDestroyed(event);

        // 获取捕获的输出
        String output = outputStream.toString();

        // 验证输出包含预期消息
        assertTrue(output.contains("=== 应用关闭 ==="), "Should log application shutdown");
    }
}
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

    @Mock
    private ServletContextEvent mockEvent;

    @Mock
    private ServletContext mockContext;

    // For capturing System.out
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new AppContextListener();

        // Setup mock behavior
        when(mockEvent.getServletContext()).thenReturn(mockContext);
        when(mockContext.getServerInfo()).thenReturn("Test Server");

        // Redirect System.out to capture output
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    @Test
    public void testContextInitialized() {
        // Call the method to test
        listener.contextInitialized(mockEvent);

        // Get captured output
        String output = outputStream.toString();

        // Verify output contains expected messages
        assertTrue(output.contains("=== 应用启动 ==="), "Should log application startup");
        assertTrue(output.contains("Server Info: Test Server"), "Should log server info");
    }

    @Test
    public void testContextDestroyed() {
        // Call the method to test
        listener.contextDestroyed(mockEvent);

        // Get captured output
        String output = outputStream.toString();

        // Verify output contains expected message
        assertTrue(output.contains("=== 应用关闭 ==="), "Should log application shutdown");
    }
}
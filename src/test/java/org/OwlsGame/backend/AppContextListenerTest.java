package org.OwlsGame.backend;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;

public class AppContextListenerTest {

    private AppContextListener listener;

    // Mock only ServletContext
    @Mock
    private ServletContext mockContext;

    // ServletContextEvent will use real instance
    private ServletContextEvent event;

    // For capturing System.out
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this);

        // Create listener instance
        listener = new AppContextListener();

        // Configure mock behavior
        when(mockContext.getServerInfo()).thenReturn("Test Server");

        // Create real ServletContextEvent using mocked ServletContext
        event = new ServletContextEvent(mockContext);

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
        // Call test method
        listener.contextInitialized(event);

        // Get captured output
        String output = outputStream.toString();

        // Verify output contains expected messages
        assertTrue(output.contains("=== Application Started ==="), "Should log application startup");
        assertTrue(output.contains("Server Info: Test Server"), "Should log server info");
    }

    @Test
    public void testContextDestroyed() {
        // Call test method
        listener.contextDestroyed(event);

        // Get captured output
        String output = outputStream.toString();

        // Verify output contains expected messages
        assertTrue(output.contains("=== Application Shutdown ==="), "Should log application shutdown");
    }
}
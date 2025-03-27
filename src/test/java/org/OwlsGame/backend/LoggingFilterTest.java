package org.OwlsGame.backend;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LoggingFilterTest {

    private LoggingFilter filter;

    @Mock
    private FilterConfig mockFilterConfig;

    @Mock
    private ServletRequest mockRequest;

    @Mock
    private ServletResponse mockResponse;

    @Mock
    private FilterChain mockFilterChain;

    // For capturing console output
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new LoggingFilter();

        // Setup mock behavior
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        // Redirect System.out to capture output
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    @Test
    void testInit() {
        // Call the method to test
        filter.init(mockFilterConfig);

        // Get captured output
        String output = outputStream.toString();

        // Verify output contains expected message
        assertTrue(output.contains("LoggingFilter initialized"),
                "Init method should log initialization message");
    }

    @Test
    void testDoFilter() throws IOException, ServletException {
        // Call the method to test
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // Get captured output
        String output = outputStream.toString();

        // Verify output contains expected patterns
        assertTrue(output.contains("Request received: 127.0.0.1 @"),
                "doFilter should log the request IP address");

        // Verify that the filter chain was continued
        verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
    }

    @Test
    void testDestroy() {
        // Call the method to test
        filter.destroy();

        // Get captured output
        String output = outputStream.toString();

        // Verify output contains expected message
        assertTrue(output.contains("LoggingFilter destroyed"),
                "Destroy method should log destruction message");
    }
}
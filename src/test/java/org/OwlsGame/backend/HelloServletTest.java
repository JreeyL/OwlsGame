package org.OwlsGame.backend;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HelloServletTest {

    private HelloServlet servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Create a new instance of the servlet
        servlet = new HelloServlet();

        // Setup response writer
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void testDoGet() throws ServletException {
        // 移除IOException，与Servlet实现保持一致
        // Execute the doGet method
        servlet.doGet(request, response);

        // Verify content type was set correctly
        verify(response).setContentType("text/plain");

        // Flush to ensure all content is written
        writer.flush();

        // Get the response content
        String content = stringWriter.toString();

        // Verify the response contains expected text
        assertTrue(content.contains("Hello from Spring Boot Servlet!"),
                "Response should contain the hello message");

        // More specific check - exact match with newline
        assertEquals("Hello from Spring Boot Servlet!" + System.lineSeparator(), content,
                "Response should exactly match expected output");
    }

    @Test
    void testServletAnnotation() {
        // Verify the servlet has the correct URL mapping
        WebServlet annotation = HelloServlet.class.getAnnotation(WebServlet.class);
        assertEquals("/hello", annotation.value()[0],
                "Servlet should be mapped to /hello URL pattern");
    }

    @Test
    void testDoGet_WithIOException() throws ServletException {
        // 测试IOException处理情况
        try {
            // 模拟getWriter抛出IOException
            when(response.getWriter()).thenThrow(new IOException("模拟IO异常"));

            // 执行doGet方法 - 不应抛出异常
            servlet.doGet(request, response);

            // 验证响应状态码被设置为500
            verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            // 设置mock时可能抛出的异常，而非servlet执行时的异常
        }
    }
}
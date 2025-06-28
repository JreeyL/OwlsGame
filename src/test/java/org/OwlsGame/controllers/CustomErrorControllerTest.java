package org.OwlsGame.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomErrorControllerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CustomErrorController errorController;

    @BeforeEach
    void setUp() {
        // Common setup if needed
    }

    @Test
    void handleError_Should_RedirectTo404Page_When_StatusIs404() {
        // Arrange
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(404);

        // Act
        String viewName = errorController.handleError(request);

        // Assert
        assertEquals("redirect:/static/error/404.html", viewName,
                "Should redirect to 404 error page for 404 status code");
    }

    @Test
    void handleError_Should_RedirectTo500Page_When_StatusIs500() {
        // Arrange
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(500);

        // Act
        String viewName = errorController.handleError(request);

        // Assert
        assertEquals("redirect:/static/error/500.html", viewName,
                "Should redirect to 500 error page for 500 status code");
    }

    @Test
    void handleError_Should_RedirectTo500Page_When_StatusIs403() {
        // Arrange
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(403);

        // Act
        String viewName = errorController.handleError(request);

        // Assert
        assertEquals("redirect:/static/error/500.html", viewName,
                "Should redirect to 500 error page for other status codes");
    }

    @Test
    void handleError_Should_RedirectTo500Page_When_StatusIsNull() {
        // Arrange
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);

        // Act
        String viewName = errorController.handleError(request);

        // Assert
        assertEquals("redirect:/static/error/500.html", viewName,
                "Should redirect to 500 error page when status is null");
    }
}
package com.homework.rewards.api.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final WebRequest webRequest = new ServletWebRequest(new MockHttpServletRequest());

    @Test
    void testHandleGeneralException() {
        Exception ex = new Exception("General error");
        ResponseEntity<ErrorResponse> response = handler.handleGeneralException(ex, webRequest);
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Internal Server Error", response.getBody().getError());
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Bad input");
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(ex, webRequest);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Bad Request", response.getBody().getError());
    }

    @Test
    void testHandleNotFoundException() {
        NotFoundException ex = new NotFoundException("Not found");
        ResponseEntity<ErrorResponse> response = handler.handleNotFoundException(ex, webRequest);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Not Found", response.getBody().getError());
    }
} 
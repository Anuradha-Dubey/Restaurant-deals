package com.customer.restaurantdeals.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleInvalidTimeFormat_ReturnsBadRequest() {
        ResponseEntity<Map<String, String>> response = handler.handleInvalidTimeFormat(
                new InvalidTimeFormatException("Invalid time"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().get("error").contains("Invalid time"));
    }

    @Test
    void handleRestaurantServiceUnavailable_ReturnsServiceUnavailable() {
        ResponseEntity<Map<String, String>> response = handler.handleRestaurantServiceUnavailable(
                new RestaurantServiceUnavailableException("Service down"));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertTrue(response.getBody().get("error").contains("Service down"));
    }

    @Test
    void handleGenericException_ReturnsInternalServerError() {
        ResponseEntity<Map<String, String>> response = handler.handleGenericException(
                new RuntimeException("fail"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody().get("error"));
    }
}

package com.customer.restaurantdeals.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleGenericException_ReturnsInternalServerError() {
        ResponseEntity<Map<String, String>> response = handler.handleGenericException(
                new RuntimeException("fail"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody().get("error"));
    }

    @Test
    void handleInvalidTimeFormat() {
        // Arrange
        InvalidTimeFormatException ex = new InvalidTimeFormatException("Invalid format");

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleInvalidTimeFormat(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Invalid format");
    }

    @Test
    void handleRestaurantServiceUnavailable() {
        // Arrange
        RestaurantServiceUnavailableException ex = new RestaurantServiceUnavailableException("Service down");

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleRestaurantServiceUnavailable(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).containsEntry("error", "Service down");
    }

    @Test
    void handleGenericException() {
        // Arrange
        RuntimeException ex = new RuntimeException("oops");

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleGenericException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("error", "An unexpected error occurred.");
    }

    @Test
    void handleNotFound() {
        // Arrange
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/unknown", new HttpHeaders());

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleNotFound(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("error", "Resource not found");
    }

    @Test
    void handleValidationErrors() {
        // Arrange
        WebExchangeBindException ex = Mockito.mock(WebExchangeBindException.class);
        FieldError fe1 = new FieldError("obj", "field1", "must not be blank");
        FieldError fe2 = new FieldError("obj", "field2", "must be positive");
        Mockito.when(ex.getFieldErrors()).thenReturn(List.of(fe1, fe2));

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleValidationErrors(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .containsEntry("field1", "must not be blank")
                .containsEntry("field2", "must be positive");
    }

    @Test
    void handleConstraintViolation() {
        // Arrange
        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
        Path path = Mockito.mock(Path.class);
        Mockito.when(path.toString()).thenReturn("timeOfDay");
        Mockito.when(violation.getPropertyPath()).thenReturn(path);
        Mockito.when(violation.getMessage()).thenReturn("Invalid format");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleConstraintViolation(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("timeOfDay", "Invalid format");
    }

    @Test
    void handleMissingParam_OtherError() {
        // Arrange
        ServerWebInputException ex = new ServerWebInputException("Some other error");

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleMissingParam(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Some other error");
    }
}

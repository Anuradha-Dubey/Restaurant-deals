package com.customer.restaurantdeals.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

import static com.customer.restaurantdeals.util.RestaurantDealsConstant.*;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTimeFormatException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTimeFormat(InvalidTimeFormatException ex) {
        log.warn("InvalidTimeFormatException", ex);
        Map<String, String> response = new HashMap<>();
        response.put(ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestaurantServiceUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleRestaurantServiceUnavailable(RestaurantServiceUnavailableException ex) {
        log.error("Service unavailable", ex);
        Map<String, String> response = new HashMap<>();
        response.put(ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        Map<String, String> response = new HashMap<>();
        response.put(ERROR, MSG_INTERNAL_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoHandlerFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put(ERROR, "Resource not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(WebExchangeBindException ex) {
        log.warn("Response DTO validation failed", ex);
        Map<String, String> errors = new HashMap<>();
        ex.getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    // Handles ConstraintViolationException from @Validated controller method parameters
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Request parameter validation failed", ex);
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(cv -> {
            String path = cv.getPropertyPath().toString();
            // extract just the parameter name
            String field = path.substring(path.lastIndexOf('.') + 1);
            errors.put(field, cv.getMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    // Handles missing query params and type‐mismatch errors in WebFlux
    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<Map<String, String>> handleMissingParam(ServerWebInputException ex) {
        log.warn("Missing or invalid input", ex);
        Map<String, String> errors = new HashMap<>();
        String reason = ex.getReason();
        // If it's the missing timeOfDay param, we normalize the message:
        if (reason != null && reason.contains("timeOfDay")) {
            errors.put("timeOfDay", MSG_TIME_REQUIRED);
        } else {
            errors.put("error", reason);
        }
        return ResponseEntity.badRequest().body(errors);
    }
}
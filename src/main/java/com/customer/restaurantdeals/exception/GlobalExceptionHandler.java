package com.customer.restaurantdeals.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

import static com.customer.restaurantdeals.util.RestaurantDealsConstant.ERROR;
import static com.customer.restaurantdeals.util.RestaurantDealsConstant.MSG_INTERNAL_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTimeFormatException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTimeFormat(InvalidTimeFormatException ex) {
        Map<String, String> response = new HashMap<>();
        response.put(ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestaurantServiceUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleRestaurantServiceUnavailable(RestaurantServiceUnavailableException ex) {
        Map<String, String> response = new HashMap<>();
        response.put(ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
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
}
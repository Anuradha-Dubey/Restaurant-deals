package com.customer.restaurantdeals.exception;

public class RestaurantServiceUnavailableException extends RuntimeException {
    public RestaurantServiceUnavailableException(String message) {
        super(message);
    }
}

package com.customer.restaurantdeals.util;

public class RestaurantDealsConstant {
    private RestaurantDealsConstant() {}

    // Error Messages
    public static final String MSG_INVALID_TIME = "Invalid time format. Expected format: 3:00pm, 6:00pm, etc.";
    public static final String MSG_RESTAURANT_SERVICE_ERROR = "Error from restaurant service. Please try again later.";
    public static final String MSG_SERVICE_UNAVAILABLE = "Failed to fetch restaurant data. Please try again later.";
    public static final String MSG_INTERNAL_ERROR = "An unexpected error occurred.";
    public static final String ERROR = "error";
    public static final String MSG_PARSING_ERROR = "Failed to parse time: ";

    // API URIs
    public static final String BASE_URI_RESTAURANTS = "https://eccdn.com.au";
    public static final String RESTAURANTS_DATA_URI = "/misc/challengedata.json";

    // Date/Time formats
    public static final String TIME_FORMAT = "h:mma";
}

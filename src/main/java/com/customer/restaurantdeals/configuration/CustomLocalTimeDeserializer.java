package com.customer.restaurantdeals.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.customer.restaurantdeals.util.RestaurantDealsConstant.MSG_PARSING_ERROR;
import static com.customer.restaurantdeals.util.RestaurantDealsConstant.TIME_FORMAT;

public class CustomLocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(TIME_FORMAT);

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String timeStr = p.getText().trim().toUpperCase();
        try {
            return LocalTime.parse(timeStr, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new RuntimeException(MSG_PARSING_ERROR + timeStr, e);
        }
    }
}

package com.customer.restaurantdeals.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class CustomLocalTimeDeserializerTest {

    @Test
    void deserialize_ValidTimeString_ReturnsLocalTime() throws Exception {
        String json = "\"3:00PM\"";
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalTime.class, new CustomLocalTimeDeserializer());
        mapper.registerModule(module);

        LocalTime result = mapper.readValue(json, LocalTime.class);
        assertEquals(LocalTime.of(15,0), result);
    }

    @Test
    void deserialize_InvalidTimeString_ThrowsRuntimeException() {
        String json = "\"notATime\"";
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalTime.class, new CustomLocalTimeDeserializer());
        mapper.registerModule(module);

        Exception exception = assertThrows(RuntimeException.class, () ->
                mapper.readValue(json, LocalTime.class)
        );
        assertTrue(exception.getMessage().contains("Failed to parse time"));
    }
}

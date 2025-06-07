package com.customer.restaurantdeals.model;

import com.customer.restaurantdeals.configuration.CustomLocalTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;

@Setter
@Getter
public class Deal {
    @JsonProperty("objectId")
    private String dealObjectId;
    private String discount;
    private boolean dineIn;
    private boolean lightning;
    @JsonDeserialize(using = CustomLocalTimeDeserializer.class)
    private LocalTime open;
    @JsonDeserialize(using = CustomLocalTimeDeserializer.class)
    private LocalTime close;
    @JsonDeserialize(using = CustomLocalTimeDeserializer.class)
    private LocalTime start;
    @JsonDeserialize(using = CustomLocalTimeDeserializer.class)
    private LocalTime end;
    private int qtyLeft;

}

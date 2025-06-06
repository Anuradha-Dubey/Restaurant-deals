package com.customer.restaurantdeals.model;

import com.customer.restaurantdeals.configuration.CustomLocalTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Setter
@Getter
public class Restaurant {
    @JsonProperty("objectId")
    private String restaurantObjectId;
    @JsonProperty("name")
    private String restaurantName;
    @JsonProperty("address1")
    private String restaurantAddress1;
    @JsonProperty("suburb")
    private String restaurantSuburb;
    @JsonProperty("open")
    @JsonDeserialize(using = CustomLocalTimeDeserializer.class)
    private LocalTime restaurantOpen;
    @JsonProperty("close")
    @JsonDeserialize(using = CustomLocalTimeDeserializer.class)
    private LocalTime restaurantClose;
    private List<Deal> deals;

}

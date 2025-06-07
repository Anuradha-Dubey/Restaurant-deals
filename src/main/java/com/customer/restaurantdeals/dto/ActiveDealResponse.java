package com.customer.restaurantdeals.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import static com.customer.restaurantdeals.util.RestaurantDealsConstant.MSG_QTY_LEFT;

@Setter
@Getter
@Data
public class ActiveDealResponse {
    @NotBlank
    private String restaurantObjectId;
    @NotBlank
    private String restaurantName;
    @NotBlank
    private String restaurantAddress1;
    @NotBlank
    private String restaurantSuburb;
    @NotBlank
    private String restaurantOpen;
    @NotBlank
    private String restaurantClose;
    @NotBlank
    private String dealObjectId;
    @NotBlank
    private String discount;
    private boolean dineIn;
    private boolean lightning;
    @Min(value = 0, message = MSG_QTY_LEFT)
    private int qtyLeft;

}

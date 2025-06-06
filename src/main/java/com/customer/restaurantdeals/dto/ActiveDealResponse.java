package com.customer.restaurantdeals.dto;

import lombok.*;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveDealResponse {
    private String restaurantObjectId;
    private String restaurantName;
    private String restaurantAddress1;
    private String restaurantSuburb;
    private String restaurantOpen;
    private String restaurantClose;
    private String dealObjectId;
    private String discount;
    private boolean dineIn;
    private boolean lightning;
    private int qtyLeft;

}

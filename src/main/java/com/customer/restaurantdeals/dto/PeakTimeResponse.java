package com.customer.restaurantdeals.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import static com.customer.restaurantdeals.util.RestaurantDealsConstant.MSG_BLANK_END_TIME;
import static com.customer.restaurantdeals.util.RestaurantDealsConstant.MSG_BLANK_PEAK_TIME;

@Setter
@Getter
public class PeakTimeResponse {
    @NotBlank(message = MSG_BLANK_PEAK_TIME)
    private String peakTimeStart;
    @NotBlank(message = MSG_BLANK_END_TIME)
    private String peakTimeEnd;

    public PeakTimeResponse(String peakTimeStart, String peakTimeEnd) {
        this.peakTimeStart = peakTimeStart;
        this.peakTimeEnd = peakTimeEnd;
    }

}

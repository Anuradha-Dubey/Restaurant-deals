package com.customer.restaurantdeals.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PeakTimeResponse {
    private String peakTimeStart;
    private String peakTimeEnd;

    public PeakTimeResponse(String peakTimeStart, String peakTimeEnd) {
        this.peakTimeStart = peakTimeStart;
        this.peakTimeEnd = peakTimeEnd;
    }

}

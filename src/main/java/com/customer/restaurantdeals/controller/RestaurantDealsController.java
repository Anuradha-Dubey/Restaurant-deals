package com.customer.restaurantdeals.controller;

import com.customer.restaurantdeals.dto.ActiveDealResponse;
import com.customer.restaurantdeals.dto.PeakTimeResponse;
import com.customer.restaurantdeals.service.RestaurantDealsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RestaurantDealsController {

    @Autowired
    private RestaurantDealsService restaurantDealsService;

    @GetMapping("/deals")
    public Mono<ResponseEntity<Map<String, List<ActiveDealResponse>>>> getActiveDeals(
            @RequestParam String timeOfDay) {

        return restaurantDealsService.getActiveDealsAtTime(timeOfDay)
                .map(deals -> ResponseEntity.ok(Map.of("deals", deals)));
    }

    @GetMapping("/deals/peak-time")
    public Mono<ResponseEntity<PeakTimeResponse>> getPeakTimeWindow() {
        return restaurantDealsService.findPeakDealTimeWindow()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }
}

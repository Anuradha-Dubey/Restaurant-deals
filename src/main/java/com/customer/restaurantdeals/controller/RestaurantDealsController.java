package com.customer.restaurantdeals.controller;

import com.customer.restaurantdeals.dto.ActiveDealResponse;
import com.customer.restaurantdeals.dto.PeakTimeResponse;
import com.customer.restaurantdeals.service.RestaurantDealsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

import static com.customer.restaurantdeals.util.RestaurantDealsConstant.*;

@RestController
@RequestMapping("/api")
@Validated
@Slf4j
public class RestaurantDealsController {

    @Autowired
    private RestaurantDealsService restaurantDealsService;

    @GetMapping("/deals")
    public Mono<ResponseEntity<Map<String, List<ActiveDealResponse>>>> getActiveDeals(
            @Valid
            @NotBlank(message = MSG_TIME_REQUIRED)
            @Pattern(
                    regexp = REGEX_TIME_FORMAT,
                    message = MSG_INVALID_TIME
            )
            @RequestParam String timeOfDay) {

        log.info("Received request for active deals at timeOfDay={}", timeOfDay);

        return restaurantDealsService.getActiveDealsAtTime(timeOfDay)
                .doOnNext(deals -> log.debug("Found {} deals for {}", deals.size(), timeOfDay))
                .map(deals -> ResponseEntity.ok(Map.of(DEALS, deals)));
    }

    @GetMapping("/deals/peak-time")
    public Mono<ResponseEntity<PeakTimeResponse>> getPeakTimeWindow() {

        log.info("Received request for peak deal time window");

        return restaurantDealsService.findPeakDealTimeWindow()
                .doOnNext(window -> log.debug("Peak window computed: {} - {}",
                        window.getPeakTimeStart(), window.getPeakTimeEnd()))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build())
                .doOnError(ex -> log.error("Error computing peak time window", ex));
    }
}

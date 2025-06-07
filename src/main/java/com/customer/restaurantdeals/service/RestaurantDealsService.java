package com.customer.restaurantdeals.service;

import com.customer.restaurantdeals.dto.ActiveDealResponse;
import com.customer.restaurantdeals.dto.PeakTimeResponse;
import com.customer.restaurantdeals.exception.InvalidTimeFormatException;
import com.customer.restaurantdeals.exception.RestaurantServiceUnavailableException;
import com.customer.restaurantdeals.mapper.ActiveDealResponseMapper;
import com.customer.restaurantdeals.model.Deal;
import com.customer.restaurantdeals.model.RestaurantResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import static com.customer.restaurantdeals.util.RestaurantDealsConstant.*;

@Service
@Slf4j
public class RestaurantDealsService {

    private final WebClient webClient;
    private final ActiveDealResponseMapper activeDealResponseMapper;

    public RestaurantDealsService(WebClient webClient, ActiveDealResponseMapper activeDealResponseMapper) {
        this.webClient = webClient;
        this.activeDealResponseMapper = activeDealResponseMapper;
    }

    public Mono<List<ActiveDealResponse>> getActiveDealsAtTime(String timeOfDay) {

        log.info("Querying active deals at {}", timeOfDay);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT, Locale.ENGLISH);
        LocalTime queryTime;

        try {
            queryTime = LocalTime.parse(timeOfDay.toUpperCase(), formatter);
        } catch (DateTimeParseException e) {
            log.warn("Invalid time format provided: {}", timeOfDay);
            throw new InvalidTimeFormatException(MSG_INVALID_TIME);
        }

        return webClient.get()
                .uri(RESTAURANTS_DATA_URI)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> {
                            log.error("Upstream returned error status {}", response.statusCode());
                            return response.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new RestaurantServiceUnavailableException(MSG_RESTAURANT_SERVICE_ERROR)));
                        }
                )
                .bodyToMono(RestaurantResponse.class)
                .onErrorMap(
                        ex -> {
                            log.error("Error fetching restaurant data", ex);
                            return ex instanceof RuntimeException;
                        },
                        ex -> {
                            log.error("Error fetching restaurant data", ex);
                            return new RestaurantServiceUnavailableException(MSG_SERVICE_UNAVAILABLE);
                        }
                )

                .flatMapMany(response -> Flux.fromIterable(response.getRestaurants())) // unwrap list
                .filter(r -> isOpenAt(queryTime, r.getRestaurantOpen(), r.getRestaurantClose())) // filter restaurants open at queryTime
                .flatMap(r -> Flux.fromIterable(r.getDeals())
                        .filter(deal -> isDealOpenAt(deal, queryTime))
                        .map(deal -> activeDealResponseMapper.toResponse(r, deal)))
                .collectList()
                .doOnSuccess(list -> log.info("Returning {} active deals", list.size()));
    }

    private boolean isOpenAt(LocalTime queryTime, LocalTime open, LocalTime close) {
        if (open == null || close == null)
            return false;
        return !queryTime.isBefore(open) && !queryTime.isAfter(close);
    }

    private boolean isDealOpenAt(Deal deal, LocalTime queryTime) {
        LocalTime open = deal.getOpen() != null ? deal.getOpen() : deal.getStart();
        LocalTime close = deal.getClose() != null ? deal.getClose() : deal.getEnd();

        // If deal has no open/close info, include it by default (if restaurant is open)
        if (open == null || close == null) {
            return true;
        }

        return isOpenAt(queryTime, open, close);
    }

    public Mono<PeakTimeResponse> findPeakDealTimeWindow() {
        log.info("Computing peak deal time window");
        return webClient.get()
                .uri(RESTAURANTS_DATA_URI)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> {
                            log.error("Upstream returned error status {}", response.statusCode());
                            return response.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new
                                            RestaurantServiceUnavailableException(MSG_RESTAURANT_SERVICE_ERROR)));
                        }
                )
                .bodyToMono(RestaurantResponse.class)
                .onErrorMap(
                        ex -> {
                            log.error("Error fetching restaurant data for peak window", ex);
                            return ex instanceof RuntimeException;
                        },
                        ex -> {
                            log.error("Error fetching restaurant data for peak window", ex);
                            return new RestaurantServiceUnavailableException(MSG_SERVICE_UNAVAILABLE);
                        }
                )
                .flatMapMany(response -> Flux.fromIterable(response.getRestaurants()))
                .flatMap(restaurant -> Flux.fromIterable(restaurant.getDeals()))
                .collectList()
                .map(this::computePeakWindow)
                .flatMap(resp -> resp == null ? Mono.empty() : Mono.just(resp))
                .doOnSuccess(resp -> {
                if (resp != null) {
                    log.info("Peak window found: {} - {}", resp.getPeakTimeStart(), resp.getPeakTimeEnd());
                } else {
                    log.info("No overlapping peak window found");
                }
                });

    }

    private PeakTimeResponse computePeakWindow(List<Deal> deals) {
        if (deals == null || deals.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT, Locale.ENGLISH);

        // Start with max time range
        LocalTime latestStart = LocalTime.MIN; // 00:00
        LocalTime earliestEnd = LocalTime.MAX; // 23:59:59.999...

        for (Deal deal : deals) {
            LocalTime start = deal.getOpen() != null ? deal.getOpen() : deal.getStart();
            LocalTime end = deal.getClose() != null ? deal.getClose() : deal.getEnd();

            if (start == null || end == null) {
                // Skip deals without time info for intersection logic
                continue;
            }

            if (start.isAfter(latestStart)) {
                latestStart = start;
            }
            if (end.isBefore(earliestEnd)) {
                earliestEnd = end;
            }
        }

        // Check if intersection exists
        if (!latestStart.isBefore(earliestEnd)) {
            return null; // No overlapping window found
        }

        return new PeakTimeResponse(
                latestStart.format(formatter),
                earliestEnd.format(formatter)
        );
    }

}
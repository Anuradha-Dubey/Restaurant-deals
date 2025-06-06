package com.customer.restaurantdeals.controller;

import com.customer.restaurantdeals.dto.ActiveDealResponse;
import com.customer.restaurantdeals.dto.PeakTimeResponse;
import com.customer.restaurantdeals.exception.InvalidTimeFormatException;
import com.customer.restaurantdeals.service.RestaurantDealsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(RestaurantDealsController.class)
@Import(RestaurantDealsControllerTest.MockConfig.class)
class RestaurantDealsControllerTest {

    @Autowired
    private RestaurantDealsService restaurantDealsService;

    @Autowired
    private WebTestClient webTestClient;

    static class MockConfig {
        @Bean
        public RestaurantDealsService restaurantDealsService() {
            return Mockito.mock(RestaurantDealsService.class);
        }
    }
    @Test
    void getActiveDeals_HappyPath() {
        List<ActiveDealResponse> deals = List.of(new ActiveDealResponse());
        when(restaurantDealsService.getActiveDealsAtTime(anyString())).thenReturn(Mono.just(deals));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/api/deals")
                        .queryParam("timeOfDay", "5:00pm")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.deals").isArray();
    }

    @Test
    void getActiveDeals_Failure_InvalidTime() {
        when(restaurantDealsService.getActiveDealsAtTime(anyString()))
                .thenThrow(new InvalidTimeFormatException("Invalid time format"));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/api/deals")
                        .queryParam("timeOfDay", "badTime")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

/*    @Test
    void getActiveDeals_Failure_ServiceUnavailable() {
        // Make sure the time string is valid so parsing passes!
        when(restaurantDealsService.getActiveDealsAtTime(anyString()))
                .thenThrow(new RestaurantServiceUnavailableException("Service unavailable"));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/api/deals")
                        .queryParam("timeOfDay", "5:00pm") // valid!
                        .build())
                .exchange()
                .expectStatus().isEqualTo(503);
    }*/

    @Test
    void getPeakTimeWindow_HappyPath() {
        PeakTimeResponse response = new PeakTimeResponse("5:00pm", "7:00pm");
        when(restaurantDealsService.findPeakDealTimeWindow()).thenReturn(Mono.just(response));

        webTestClient.get().uri("/api/deals/peak-time")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.peakTimeStart").isEqualTo("5:00pm")
                .jsonPath("$.peakTimeEnd").isEqualTo("7:00pm");
    }

    @Test
    void getPeakTimeWindow_Empty() {
        when(restaurantDealsService.findPeakDealTimeWindow()).thenReturn(Mono.empty());

        webTestClient.get().uri("/api/deals/peak-time")
                .exchange()
                .expectStatus().isNoContent();
    }

}

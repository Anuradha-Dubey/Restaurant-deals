package com.customer.restaurantdeals.controller;

import com.customer.restaurantdeals.dto.ActiveDealResponse;
import com.customer.restaurantdeals.dto.PeakTimeResponse;
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
import java.util.Map;
import static com.customer.restaurantdeals.util.RestaurantDealsConstant.MSG_TIME_REQUIRED;
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
        List<ActiveDealResponse> deals = List.of(validDeal());
        when(restaurantDealsService.getActiveDealsAtTime("5:00pm")).thenReturn(Mono.just(deals));

        webTestClient.get()
                .uri(uri -> uri.path("/api/deals").queryParam("timeOfDay", "5:00pm").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.deals").isArray();
    }

    @Test
    void getActiveDeals_Failure_MissingTimeParam() {
        webTestClient.get().uri(uri -> uri.path("/api/deals").build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    Map<String, String> errors = response.getResponseBody();
                    assert errors.containsKey("timeOfDay");
                    assert errors.get("timeOfDay").equals(MSG_TIME_REQUIRED);
                });
    }

    @Test
    void getActiveDeals_Failure_InvalidFormat() {
        webTestClient.get()
                .uri(uri -> uri.path("/api/deals").queryParam("timeOfDay", "13:00pm").build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    Map<String, String> errors = response.getResponseBody();
                    assert errors.containsKey("timeOfDay");
                    assert errors.get("timeOfDay")
                            .equals("Invalid time format. Expected format: 3:00pm, 6:00pm, etc.");
                });
    }

    @Test
    void getPeakTimeWindow_HappyPath() {
        PeakTimeResponse response = new PeakTimeResponse("5:00PM", "7:00PM");
        when(restaurantDealsService.findPeakDealTimeWindow()).thenReturn(Mono.just(response));

        webTestClient.get().uri("/api/deals/peak-time")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.peakTimeStart").isEqualTo("5:00PM")
                .jsonPath("$.peakTimeEnd").isEqualTo("7:00PM");
    }

    @Test
    void getPeakTimeWindow_Empty() {
        when(restaurantDealsService.findPeakDealTimeWindow()).thenReturn(Mono.empty());

        webTestClient.get().uri("/api/deals/peak-time")
                .exchange()
                .expectStatus().isNoContent();
    }

    private ActiveDealResponse validDeal() {
        ActiveDealResponse d = new ActiveDealResponse();
        d.setRestaurantObjectId("r1");
        d.setRestaurantName("Test");
        d.setRestaurantAddress1("Addr");
        d.setRestaurantSuburb("Sub");
        d.setRestaurantOpen("8:00AM");
        d.setRestaurantClose("10:00PM");
        d.setDealObjectId("d1");
        d.setDiscount("10%");
        d.setQtyLeft(5);
        return d;
    }

}


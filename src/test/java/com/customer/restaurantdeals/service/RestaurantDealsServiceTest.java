package com.customer.restaurantdeals.service;

import com.customer.restaurantdeals.dto.ActiveDealResponse;
import com.customer.restaurantdeals.dto.PeakTimeResponse;
import com.customer.restaurantdeals.exception.InvalidTimeFormatException;
import com.customer.restaurantdeals.exception.RestaurantServiceUnavailableException;
import com.customer.restaurantdeals.mapper.ActiveDealResponseMapper;
import com.customer.restaurantdeals.model.Deal;
import com.customer.restaurantdeals.model.Restaurant;
import com.customer.restaurantdeals.model.RestaurantResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantDealsServiceTest {

    private WebClient webClient;
    private ActiveDealResponseMapper mapper;
    private RestaurantDealsService service;
    private WebClient.RequestHeadersUriSpec uriSpec;
    private WebClient.RequestHeadersSpec headersSpec;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        webClient = mock(WebClient.class);
        uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        headersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(eq(RestaurantResponse.class)))
                .thenReturn(Mono.just(mockRestaurantResponse()));

        mapper = mock(ActiveDealResponseMapper.class);
        when(mapper.toResponse(any(Restaurant.class), any(Deal.class)))
                .thenReturn(new ActiveDealResponse());

        service = new RestaurantDealsService(webClient, mapper);
    }

    @Test
    void getActiveDealsAtTime_HappyPath() {
        Mono<List<ActiveDealResponse>> result = service.getActiveDealsAtTime("10:00am");
        StepVerifier.create(result)
                .expectNextMatches(list -> !list.isEmpty())
                .verifyComplete();
    }

    @Test
    void getActiveDealsAtTime_InvalidTime_ThrowsException() {
        assertThrows(InvalidTimeFormatException.class, () ->
                service.getActiveDealsAtTime("notatime").block()
        );
    }

    @Test
    void getActiveDealsAtTime_ServiceUnavailable_ThrowsException() {
        when(responseSpec.bodyToMono(eq(RestaurantResponse.class)))
                .thenReturn(Mono.error(WebClientResponseException.create(500, "Internal Server Error", null, null, null)));

        assertThrows(RestaurantServiceUnavailableException.class, () ->
                service.getActiveDealsAtTime("10:00am").block()
        );
    }

    @Test
    void findPeakDealTimeWindow_HappyPath() {
        Mono<PeakTimeResponse> result = service.findPeakDealTimeWindow();
        StepVerifier.create(result)
                .assertNext(p -> {
                    assertNotNull(p.getPeakTimeStart());
                    assertNotNull(p.getPeakTimeEnd());
                })
                .verifyComplete();
    }

    @Test
    void findPeakDealTimeWindow_ServiceUnavailable_ThrowsException() {
        when(responseSpec.bodyToMono(eq(RestaurantResponse.class)))
                .thenReturn(Mono.error(WebClientResponseException.create(500, "Internal Server Error", null, null, null)));

        assertThrows(RestaurantServiceUnavailableException.class, () ->
                service.findPeakDealTimeWindow().block()
        );
    }

    @Test
    void computePeakWindow_NoOverlap_ReturnsNull() throws Exception {
        var method = RestaurantDealsService.class.getDeclaredMethod("computePeakWindow", List.class);
        method.setAccessible(true);

        Deal d1 = new Deal();
        d1.setOpen(LocalTime.of(10, 0));
        d1.setClose(LocalTime.of(12, 0));
        Deal d2 = new Deal();
        d2.setOpen(LocalTime.of(13, 0));
        d2.setClose(LocalTime.of(15, 0));

        Object res = method.invoke(service, List.of(d1, d2));
        assertNull(res);
    }

    @Test
    void isOpenAt_VariousCases() throws Exception {
        var method = RestaurantDealsService.class.getDeclaredMethod("isOpenAt", LocalTime.class, LocalTime.class, LocalTime.class);
        method.setAccessible(true);

        LocalTime time = LocalTime.of(12, 0);

        // null open/close
        assertFalse((Boolean) method.invoke(service, time, null, null));
        // open before, close after
        assertTrue((Boolean) method.invoke(service, time, LocalTime.of(10,0), LocalTime.of(14,0)));
        // open after
        assertFalse((Boolean) method.invoke(service, time, LocalTime.of(13,0), LocalTime.of(14,0)));
        // close before
        assertFalse((Boolean) method.invoke(service, time, LocalTime.of(9,0), LocalTime.of(11,0)));
    }

    private RestaurantResponse mockRestaurantResponse() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantOpen(LocalTime.of(10, 0));
        restaurant.setRestaurantClose(LocalTime.of(16, 0));
        Deal deal = new Deal();
        deal.setOpen(LocalTime.of(10, 0));
        deal.setClose(LocalTime.of(15, 0));
        restaurant.setDeals(List.of(deal));

        RestaurantResponse resp = new RestaurantResponse();
        resp.setRestaurants(List.of(restaurant));
        return resp;
    }
}
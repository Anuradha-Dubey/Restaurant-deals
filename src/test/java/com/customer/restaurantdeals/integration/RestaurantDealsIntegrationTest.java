package com.customer.restaurantdeals.integration;

import com.customer.restaurantdeals.RestaurantDealsApplication;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(
        classes = RestaurantDealsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestaurantDealsIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private WireMockServer wireMockServer;

    @BeforeAll
    void setupWireMock() {
        wireMockServer = new WireMockServer(9090);
        wireMockServer.start();
    }

    @AfterAll
    void teardownWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void resetWireMock() {
        wireMockServer.resetAll();
    }

    @Test
    void getActiveDeals_ShouldReturnDealsList() {
        //WireMock stub for /misc/challengedata.json
        wireMockServer.stubFor(get(urlEqualTo("/misc/challengedata.json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("sample-restaurant-response.json")));

        // Act & Assert
        webTestClient.get()
                .uri("/api/deals?timeOfDay=5:00pm")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.deals").isArray();
    }

    @Test
    void getActiveDeals_ShouldReturnBadRequestForInvalidTime() {
        webTestClient.get()
                .uri("/api/deals?timeOfDay=badtime")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getPeakTimeWindow_ShouldReturnPeakWindow() {
        wireMockServer.stubFor(get(urlEqualTo("/misc/challengedata.json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("sample-restaurant-response.json")));

        webTestClient.get()
                .uri("/api/deals/peak-time")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.peakTimeStart").exists()
                .jsonPath("$.peakTimeEnd").exists();
    }

    @Test
    void malformedUri_ShouldReturnNotFound() {
        webTestClient.get()
                .uri("/api/unknown")
                .exchange()
                .expectStatus().isNotFound();
    }
}

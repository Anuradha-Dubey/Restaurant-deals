package com.customer.restaurantdeals.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import static com.customer.restaurantdeals.util.RestaurantDealsConstant.BASE_URI_RESTAURANTS;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(BASE_URI_RESTAURANTS)
                .build();
    }
}
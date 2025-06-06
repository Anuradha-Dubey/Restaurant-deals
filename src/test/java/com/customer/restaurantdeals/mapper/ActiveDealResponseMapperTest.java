package com.customer.restaurantdeals.mapper;

import com.customer.restaurantdeals.dto.ActiveDealResponse;
import com.customer.restaurantdeals.model.Deal;
import com.customer.restaurantdeals.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class ActiveDealResponseMapperTest {

    private final ActiveDealResponseMapper mapper = Mappers.getMapper(ActiveDealResponseMapper.class);

    @Test
    void toResponse_MapsFieldsCorrectly() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantObjectId("r1");
        restaurant.setRestaurantName("KFC");
        restaurant.setRestaurantAddress1("address");
        restaurant.setRestaurantSuburb("suburb");
        restaurant.setRestaurantOpen(LocalTime.of(9,0));
        restaurant.setRestaurantClose(LocalTime.of(18,0));

        Deal deal = new Deal();
        deal.setDealObjectId("d1");
        deal.setDiscount("10%");
        deal.setDineIn(true);
        deal.setLightning(false);
        deal.setQtyLeft(5);

        ActiveDealResponse response = mapper.toResponse(restaurant, deal);

        assertEquals("r1", response.getRestaurantObjectId());
        assertEquals("KFC", response.getRestaurantName());
        assertEquals("address", response.getRestaurantAddress1());
        assertEquals("suburb", response.getRestaurantSuburb());
        assertEquals("9:00am", response.getRestaurantOpen());
        assertEquals("6:00pm", response.getRestaurantClose());
        assertEquals("d1", response.getDealObjectId());
        assertEquals("10%", response.getDiscount());
        assertTrue(response.isDineIn());
        assertFalse(response.isLightning());
        assertEquals(5, response.getQtyLeft());
    }

    @Test
    void mapLocalTimeToString_Null_ReturnsNull() {
        assertNull(mapper.mapLocalTimeToString(null));
    }
}

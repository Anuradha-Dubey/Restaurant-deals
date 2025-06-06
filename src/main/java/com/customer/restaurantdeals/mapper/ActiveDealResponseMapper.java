package com.customer.restaurantdeals.mapper;

import com.customer.restaurantdeals.dto.ActiveDealResponse;
import com.customer.restaurantdeals.model.Deal;
import com.customer.restaurantdeals.model.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface ActiveDealResponseMapper {

    @Mapping(target = "restaurantOpen", source = "restaurant.restaurantOpen", qualifiedByName = "localTimeToString")
    @Mapping(target = "restaurantClose", source = "restaurant.restaurantClose", qualifiedByName = "localTimeToString")
    ActiveDealResponse toResponse(Restaurant restaurant, Deal deal);

    @Named("localTimeToString")
    default String mapLocalTimeToString(LocalTime time) {
        if (time == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma");
        return time.format(formatter).toLowerCase();
    }
}

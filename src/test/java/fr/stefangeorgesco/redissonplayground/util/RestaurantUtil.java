package fr.stefangeorgesco.redissonplayground.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.stefangeorgesco.redissonplayground.dto.Restaurant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class RestaurantUtil {

    private static final Logger log = LoggerFactory.getLogger(RestaurantUtil.class);

    private RestaurantUtil() {
    }

    public static List<Restaurant> getRestaurants() {
        ObjectMapper mapper = new ObjectMapper();
        InputStream stream = RestaurantUtil.class.getClassLoader().getResourceAsStream("restaurant.json");
        try {
            return mapper.readValue(stream, new TypeReference<>() {
            });
        } catch (IOException e) {
            log.error("Error reading restaurant.json: {}", e.getMessage());
            return List.of();
        }
    }
}

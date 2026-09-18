package fr.stefangeorgesco.redissonplayground;

import fr.stefangeorgesco.redissonplayground.dto.GeoLocation;
import fr.stefangeorgesco.redissonplayground.dto.Restaurant;
import fr.stefangeorgesco.redissonplayground.util.RestaurantUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.api.geo.GeoUnit;
import org.redisson.api.geo.OptionalGeoSearch;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Function;

/*
    Ensure that the Redis server is running and accessible at the configured host and port
    with the correct credentials (if any) before running these tests.
    Type "flushdb" in redis-cli to clear the database before running the tests to avoid conflicts with existing data.
 */

class Lec17GeoSpatialTests extends BaseTests {

    private RGeoReactive<Restaurant> geo;
    private RMapReactive<String, GeoLocation> map;

    @BeforeAll
    void setGeo() {
        geo = client.getGeo("restaurants", new TypedJsonJacksonCodec(Restaurant.class));
        map = client.getMap("us:texas", new TypedJsonJacksonCodec(String.class, GeoLocation.class));
        List<Restaurant> restaurants = RestaurantUtil.getRestaurants();
        log.info("Found {} restaurant entries in file", restaurants.size());
        log.info("Found {} distinct restaurant ids", restaurants
                .stream()
                .map(Restaurant::id)
                .distinct()
                .count());
        log.info("Found {} distinct restaurant datasets", restaurants
                .stream()
                .map(Restaurant::toString)
                .distinct()
                .count());
        Flux.fromIterable(restaurants)
                .flatMap(r -> geo.add(r.longitude(), r.latitude(), r).thenReturn(r))
                .flatMap(r -> map.fastPut(r.id(), GeoLocation.of(r.latitude(), r.longitude())))
                .subscribe();
    }

    @Test
    void searchByGeoSearch() {
        /*{
            "id": "AVwcgtLzByjofQCxfCD-",
            "city": "Dallas",
            "latitude": 32.78136,
            "longitude": -96.80539,
            "name": "McDonald's",
            "zip": 75202
          }*/
        double longitude = -96.80539;
        double latitude = 32.78136;
        double radius = 3.0;
        GeoUnit unit = GeoUnit.MILES;

        OptionalGeoSearch geoSearch = GeoSearchArgs.from(longitude, latitude).radius(radius, unit);

        geo.search(geoSearch)
                .flatMapIterable(Function.identity())
                .doOnNext(restaurant ->
                        log.info("Found restaurant: {} less than {} {} away from longitude: {} latitude: {}",
                                restaurant, radius, unit, longitude, latitude))
                .count()
                .doOnNext(count -> log.info("Found {} restaurants less than {} {} away from longitude: {} latitude: {}",
                        count, radius, unit, longitude, latitude))
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void searchByRestaurantId() {
        /*{
            "id": "AVwdXdB6IN2L1WUfvERL",
            "city": "Dallas",
            "latitude": 32.704867,
            "longitude": -96.82811,
            "name": "McDonald's",
            "zip": 75224
          }*/
        String id = "AVwdXdB6IN2L1WUfvERL";
        double radius = 5.0;
        GeoUnit unit = GeoUnit.MILES;

        map.get(id)
                .doOnNext(geoLocation ->
                        log.info("Found geo location longitude: {} latitude: {} for restaurant with id: {}",
                                geoLocation.longitude(), geoLocation.latitude(), id))
                .map(geoLocation -> GeoSearchArgs.from(geoLocation.longitude(), geoLocation.latitude())
                        .radius(radius, unit))
                .flatMap(geo::search)
                .flatMapIterable(Function.identity())
                .doOnNext(restaurant -> log.info(
                        "Found restaurant: {} less than {} {} away from selected restaurant",
                        restaurant, radius, unit))
                .count()
                .doOnNext(count -> log.info("Found {} restaurants less than {} {} away from selected restaurant",
                        count, radius, unit))
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }
}
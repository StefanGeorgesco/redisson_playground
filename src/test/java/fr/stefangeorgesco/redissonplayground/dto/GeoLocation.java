package fr.stefangeorgesco.redissonplayground.dto;

public record GeoLocation(double latitude,
                          double longitude) {

    public static GeoLocation of(double latitude, double longitude) {
        return new GeoLocation(latitude, longitude);
    }
}

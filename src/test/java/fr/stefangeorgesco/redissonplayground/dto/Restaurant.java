package fr.stefangeorgesco.redissonplayground.dto;

public record Restaurant(String id,
                         String city,
                         double latitude,
                         double longitude,
                         String name,
                         int zip) {
}

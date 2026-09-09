package fr.stefangeorgesco.redissonplayground.assignment;

import java.util.Random;

public enum Category {
    PRIME,
    STD,
    GUEST;

    public static Category getRandomCategory() {
        int randomIndex = new Random().nextInt(values().length);
        return values()[randomIndex];
    }
}

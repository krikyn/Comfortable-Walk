package com.netcracker.features.util;

import java.util.Arrays;

/**
 * Enum for Checked items
 *
 * @author prokhorovartem
 */
public enum PlacesType {
    AMUSEMENT_PARK("AMUSEMENT"),
    BAKERY("BAKERY"),
    BAR("BAR"),
    BOOK_STORE("BOOK"),
    CAFE("CAFE"),
    CLOTHING_STORE("CLOTHING"),
    ELECTRONICS_STORE("ELECTRONICS STORE"),
    GROCERY_OR_SUPERMARKET("GROCERY OR SUPERMARKET"),
    LAUNDRY("LAUNDRY"),
    MOVIE_THEATER("MOVIE THEATER"),
    MUSEUM("MUSEUM"),
    PARK("PARK"),
    PHARMACY("PHARMACY"),
    POST_OFFICE("POST OFFICE"),
    RESTAURANT("RESTAURANT"),
    SHOPPING_MALL("SHOPPING MALL"),
    STADIUM("STADIUM"),
    ZOO("ZOO");

    private final String token;

    PlacesType(String type) {
        token = type;
    }

    /**
     * Getting string and return enum type
     *
     * @param string string, which is taken from mini-search from frontend
     * @return enum type or null
     */
    public static PlacesType getType(String string) {
        return Arrays.stream(PlacesType.values()).filter(type -> string.contains(type.token)).findFirst().orElse(null);
    }
}

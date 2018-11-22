package com.netcracker.util;

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

    public static PlacesType getType(String string) {
        for (PlacesType type : PlacesType.values()) {
            if (string.contains(type.token)) return type;
        }
        return null;
    }
}

package com.netcracker.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CheckedItemsUtil {
    //Ужасный костыль, так как нет времени сделать нормальный поиск
    public ArrayList<PlacesType> getItems(String string) {
        ArrayList<PlacesType> list = new ArrayList<>();
        String[] arrayOfWords = string.split(" ");
        for (int i = 0; i < arrayOfWords.length; i++) {
            try {
                list.add(PlacesType.valueOf(arrayOfWords[i]));
            } catch (IllegalArgumentException e) {
                if (arrayOfWords[i].contains("GROCERY") | arrayOfWords[i].contains("SUPERMARKET"))
                    list.add(PlacesType.GROCERY_OR_SUPERMARKET);
                else {
                    String twoWordedPlace = arrayOfWords[i].concat(" ").concat(arrayOfWords[i + 1]);
                    if (twoWordedPlace.equals("AMUSEMENT PARK")) {
                        list.add(PlacesType.AMUSEMENT_PARK);
                        i++;
                    }
                    if (twoWordedPlace.equals("BOOK STORE")) {
                        list.add(PlacesType.BOOK_STORE);
                        i++;
                    }
                    if (twoWordedPlace.equals("CLOTHING STORE")) {
                        list.add(PlacesType.CLOTHING_STORE);
                        i++;
                    }
                    if (twoWordedPlace.equals("ELECTRONICS STORE")) {
                        list.add(PlacesType.ELECTRONICS_STORE);
                        i++;
                    }
                    if (twoWordedPlace.equals("MOVIE THEATER")) {
                        list.add(PlacesType.MOVIE_THEATER);
                        i++;
                    }
                    if (twoWordedPlace.equals("POST OFFICE")) {
                        list.add(PlacesType.POST_OFFICE);
                        i++;
                    }
                    if (twoWordedPlace.equals("SHOPPING MALL")) {
                        list.add(PlacesType.SHOPPING_MALL);
                        i++;
                    }
                }
            }
        }
        return list;
    }
}

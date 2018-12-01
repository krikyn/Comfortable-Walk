package com.netcracker.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Util for checked items in mini-search in main-page
 * @author prokhorovartem
 */
@Component
public class CheckedItemsUtil {
    /**
     * Getting items from the string
     * @param string the string that user wrote
     * @return list of items, user checked
     */
    //Ужасный костыль, так как нет времени сделать нормальный поиск
    public ArrayList<PlacesType> getItems(String string) {
        ArrayList<PlacesType> list = new ArrayList<>();
        String[] arrayOfWords = string.split(",");
        for (String arrayOfWord : arrayOfWords) {
            try {
                list.add(PlacesType.getType(arrayOfWord));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
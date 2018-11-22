package com.netcracker.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CheckedItemsUtil {
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
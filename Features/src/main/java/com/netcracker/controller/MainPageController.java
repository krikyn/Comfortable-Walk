package com.netcracker.controller;

import com.netcracker.data.model.bean.Path;
import com.netcracker.util.CheckedItemsUtil;
import com.netcracker.util.PlacesType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * Controller for main page
 * @author prokhorovartem
 */
@RequiredArgsConstructor
@RestController
public class MainPageController {

    private final CheckedItemsUtil checkedItemsUtil;

    @PostMapping("/sendData")
    public double[][] sendData(@RequestBody Path path) {
        List<PlacesType> checkedItems;
        if (path.getPlaceName() == null)
            checkedItems = null;
        else
            checkedItems = checkedItemsUtil.getItems(path.getPlaceName().toUpperCase());
        System.out.println("From point: " + path.getFromPointLat() + ", " + path.getFromPointLng()
                + " To Point: " + path.getToPointLat() + ", " + path.getToPointLng()
                + " Wanna best weather? " + path.getIsBestWeather()
                + " Checked items: " + checkedItems);
        double[][] response = {
                {59.971304, 30.293745},
                {59.966297, 30.292687}
        };
        return response;
    }
}

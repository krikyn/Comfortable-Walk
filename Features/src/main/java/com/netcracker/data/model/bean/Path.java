package com.netcracker.data.model.bean;

import com.netcracker.util.PlacesType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Path bean for saving path params
 * @author prokhorovartem
 */
@Getter
@Setter
public class Path {
    private String fromPointLng;
    private String fromPointLat;
    private String toPointLng;
    private String toPointLat;
    private List<PlacesType> checkedItems;
    private String placeName;
    private Boolean isBestWeather;
}

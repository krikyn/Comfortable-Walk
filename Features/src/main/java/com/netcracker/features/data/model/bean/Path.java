package com.netcracker.features.data.model.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * Path bean for saving path params
 *
 * @author prokhorovartem
 */
@Getter
@Setter
public class Path {
    private String fromPointLng;
    private String fromPointLat;
    private String toPointLng;
    private String toPointLat;
    private String placeName;
    private Boolean isBestWeather;
}

package com.netcracker.data.model.bean;

import com.netcracker.util.PlacesType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Path {
    private String fromPoint;
    private String toPoint;
    private List<PlacesType> checkedItems;
    private String types;
    private Boolean isBestWeather;
}

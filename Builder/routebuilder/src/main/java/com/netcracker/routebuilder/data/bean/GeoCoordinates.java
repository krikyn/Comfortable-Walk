package com.netcracker.routebuilder.data.bean;

import lombok.Data;

@Data
public class GeoCoordinates {

    private Double x;
    private Double y;

    public GeoCoordinates(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

}

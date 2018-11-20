package com.netcracker.routebuilder.data.bean;

public class GeoCoordinates {
    private Double x;
    private Double y;

    public GeoCoordinates(Double x, Double y){
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }
}

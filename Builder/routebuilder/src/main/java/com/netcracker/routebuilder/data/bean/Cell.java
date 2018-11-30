package com.netcracker.routebuilder.data.bean;


import lombok.Data;

@Data
public class Cell {

    private int value;
    private Cell parent;
    private boolean isClosed;
    private FieldCoordinates fieldCoordinates;
    private GeoCoordinates geoCoordinates;

    private Double fx;
    private Double gx;
    private Double hx;

    public Cell(int value, FieldCoordinates fieldCoordinates, GeoCoordinates geoCoordinates){
        this.value = value;
        this.fieldCoordinates = fieldCoordinates;
        this.geoCoordinates = geoCoordinates;
        gx = Double.MAX_VALUE;
        parent = null;
        isClosed = false;
    }

    public boolean isClosed(){
        return isClosed;
    }

    public void updateParameters(Cell parent, Double gx, Double hx){
        this.parent = parent;
        this.fx = gx + hx;
        this.gx = gx;
        this.hx = hx;
    }

}

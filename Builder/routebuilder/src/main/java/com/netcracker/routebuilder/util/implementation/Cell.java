package com.netcracker.routebuilder.util.implementation;

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

    public Double getFx(){
        return fx;
    }

    public FieldCoordinates getFieldCoordinates() {
        return fieldCoordinates;
    }

    public GeoCoordinates getGeoCoordinates() {
        return geoCoordinates;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public Double getGx() {
        return gx;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value){
        this.value = value;
    }

    public Cell getParent(){
        return parent;
    }
}

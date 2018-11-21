package com.netcracker.routebuilder.data.bean;

import lombok.Data;
@Data
public class FieldCoordinates {

    private int x;
    private int y;

    public FieldCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

}

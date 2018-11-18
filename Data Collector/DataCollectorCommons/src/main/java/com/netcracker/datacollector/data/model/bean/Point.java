package com.netcracker.datacollector.data.model.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Point {
    private double x;
    private double y;

    @Override
    public String toString() {
        return y + ", " + x;
    }
}

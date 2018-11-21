package com.netcracker.datacollector.data.model.bean;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Point {
    private BigDecimal x;
    private BigDecimal y;

    @Override
    public String toString() {
        return y + ", " + x;
    }
}

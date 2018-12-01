package com.netcracker.commons.data.model.bean;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Point bean for saving point params and overridden toString()
 * @author prokhorovartem
 */
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

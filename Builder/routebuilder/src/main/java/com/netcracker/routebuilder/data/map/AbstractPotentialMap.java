package com.netcracker.routebuilder.data.map;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class describing a potential map
 *
 * @author Kirill.Vakhrushev
 */
public abstract class AbstractPotentialMap {

    /**
     * Potential map
     */
    @Getter
    @Setter
    protected int[][] field;

    /**
     * @param x row
     * @param y column
     * @return potential map cell value
     */
    public int get(int x, int y) {
        return field[x][y];
    }
}

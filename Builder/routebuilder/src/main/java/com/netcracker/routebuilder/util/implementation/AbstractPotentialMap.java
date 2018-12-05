package com.netcracker.routebuilder.util.implementation;

import lombok.Getter;

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

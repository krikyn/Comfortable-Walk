package com.netcracker.routebuilder.util.interfaces;

public abstract class AbstractPotentialMap {

    protected int[][] field;

    public int get(int x, int y) {
        //доделать проверку
        //Utils.checkBorders(x, y, params.getScale());
        return field[x][y];
    }

    public int[][] getField() {
        return field;
    }
}

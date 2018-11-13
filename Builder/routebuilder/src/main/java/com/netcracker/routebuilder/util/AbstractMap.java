package com.netcracker.routebuilder.util;

import java.util.ArrayList;

import static com.netcracker.routebuilder.util.Utils.recountWithNewScale;

public abstract class AbstractMap implements Map {
    private ArrayList<ArrayList<Integer>> field;
    private int scale;

    abstract void fillField(int scale);

    AbstractMap(int scale) {
        this.scale = scale;

        field = new ArrayList<>();
        final int defaultNumPointsX = 21;
        final int defaultNumPointsY = 20;
        final int numPointsX = recountWithNewScale(defaultNumPointsX, scale);
        final int numPointsY = recountWithNewScale(defaultNumPointsY, scale);

        for (int i = 0; i < numPointsX; i++) {
            field.add(new ArrayList<Integer>());
            for (int j = 0; j < numPointsY; j++) {
                field.get(i).add(0);
            }
        }

        fillField(scale);
    }

    @Override
    public int get(int x, int y) {
        Utils.checkBorders(x, y, scale);
        return field.get(x).get(y);
    }
}

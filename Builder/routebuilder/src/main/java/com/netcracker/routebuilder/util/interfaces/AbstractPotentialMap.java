package com.netcracker.routebuilder.util.interfaces;

import com.netcracker.routebuilder.util.implementation.Utils;

import java.util.ArrayList;

import static com.netcracker.routebuilder.util.implementation.Utils.recountWithNewScale;

public abstract class AbstractPotentialMap implements PotentialMap {
    private ArrayList<ArrayList<Integer>> field;
    private int scale;

    abstract public int valueOf(int x, int y, int scale);

    public AbstractPotentialMap(int scale) {
        this.scale = scale;

        field = new ArrayList<>();
        final int defaultNumPointsX = 21;
        final int defaultNumPointsY = 20;
        final int numPointsX = recountWithNewScale(defaultNumPointsX, scale);
        final int numPointsY = recountWithNewScale(defaultNumPointsY, scale);

        for (int i = 0; i < numPointsX; i++) {
            field.add(new ArrayList<>());
            for (int j = 0; j < numPointsY; j++) {
                field.get(i).add(valueOf(i,j,scale));
            }
        }
    }

    @Override
    public int get(int x, int y) {
        Utils.checkBorders(x, y, scale);
        return field.get(x).get(y);
    }
}

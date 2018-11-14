package com.netcracker.routebuilder.util.implementation;

import com.netcracker.routebuilder.util.interfaces.AbstractPotentialMap;

public class WeatherMap extends AbstractPotentialMap {

    public WeatherMap(int scale){
        super(scale);
    }

    int[][] map;


    @Override
    public int valueOf(int x, int y, int scale) {
        return 0;
    }
}

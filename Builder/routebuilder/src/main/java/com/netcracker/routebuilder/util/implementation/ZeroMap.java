package com.netcracker.routebuilder.util.implementation;

import com.netcracker.routebuilder.util.interfaces.AbstractPotentialMap;

public class ZeroMap extends AbstractPotentialMap {

    public ZeroMap(int scale) {
        super(scale);
    }

    @Override
    public int valueOf(int x, int y, int scale) {
        return 0;
    }

}

package com.netcracker.routebuilder.algorithm.interfaces;

import com.netcracker.routebuilder.util.interfaces.AbstractPotentialMap;
import com.netcracker.routebuilder.util.enums.RouteProperties;

import java.util.ArrayList;

public interface PotentialMapBuilder {

    public AbstractPotentialMap assemblePotentialMap(ArrayList<RouteProperties> includedProperties);

}

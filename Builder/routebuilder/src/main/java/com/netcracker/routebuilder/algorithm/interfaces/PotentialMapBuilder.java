package com.netcracker.routebuilder.algorithm.interfaces;

import com.netcracker.routebuilder.util.AbstractMap;
import com.netcracker.routebuilder.util.utils.RouteProperties;

import java.util.ArrayList;

public interface PotentialMapBuilder {

    public AbstractMap assemblePotentialMap(ArrayList<RouteProperties> includedProperties);

}

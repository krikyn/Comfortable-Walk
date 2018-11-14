package com.netcracker.routebuilder.algorithm.interfaces;

import com.netcracker.routebuilder.util.implementation.GeoCoordinates;
import com.netcracker.routebuilder.util.enums.RouteProperties;

import java.util.ArrayList;

public interface PathFindingAlgorithm {

    public ArrayList<GeoCoordinates> buildRoute(GeoCoordinates start, GeoCoordinates end, ArrayList<RouteProperties> routeProperties);

}

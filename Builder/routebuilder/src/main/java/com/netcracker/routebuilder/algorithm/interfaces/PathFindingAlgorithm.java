package com.netcracker.routebuilder.algorithm.interfaces;

import com.netcracker.routebuilder.util.GeoCoordinates;
import com.netcracker.routebuilder.util.utils.RouteProperties;

import java.util.ArrayList;

public interface PathFindingAlgorithm {

    public ArrayList<GeoCoordinates> buildRoute(GeoCoordinates start, GeoCoordinates end, ArrayList<RouteProperties> routeProperties);

}

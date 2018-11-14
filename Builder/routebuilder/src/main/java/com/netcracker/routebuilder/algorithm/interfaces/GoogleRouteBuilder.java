package com.netcracker.routebuilder.algorithm.interfaces;

import com.netcracker.routebuilder.util.implementation.GeoCoordinates;

import java.util.ArrayList;

public interface GoogleRouteBuilder {

    public ArrayList<GeoCoordinates> buildRoute(GeoCoordinates start, GeoCoordinates end);
    public ArrayList<GeoCoordinates> buildRoute(GeoCoordinates start,ArrayList<GeoCoordinates> points, GeoCoordinates end);

}

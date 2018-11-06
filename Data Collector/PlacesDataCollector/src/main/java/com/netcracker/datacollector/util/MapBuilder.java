package com.netcracker.datacollector.util;

import com.google.maps.model.LatLng;
import com.netcracker.datacollector.data.model.Place;

import java.util.List;

public interface MapBuilder {
    LatLng[][] buildBaseMap(int scale);
    int[][] buildPlaceMap(LatLng[][] baseMap, List<Place> places, int scale);
    int[][] buildPotentialMap(int[][] placeMap, int scale);
}

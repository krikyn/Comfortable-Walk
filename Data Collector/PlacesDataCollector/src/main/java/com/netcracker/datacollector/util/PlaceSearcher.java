package com.netcracker.datacollector.util;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;

public interface PlaceSearcher {

    PlacesSearchResult findPlaceFromText(String place) throws Exception;
    PlaceDetails getPlaceDetails(String place) throws Exception;
    PlacesSearchResult[] findNearbyPlaces(LatLng location, int radius, String type) throws Exception;
    PlacesSearchResponse findAllPlacesByType(String type) throws Exception;
    PlacesSearchResponse findAllPlacesByType(String type, String token) throws Exception;
}

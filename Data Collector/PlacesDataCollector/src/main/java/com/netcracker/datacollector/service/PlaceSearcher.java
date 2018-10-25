package com.netcracker.datacollector.service;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;

public interface PlaceSearcher {

    PlacesSearchResult findPlaceFromText(String place) throws Exception;
    PlaceDetails getPlaceDetails(String place) throws Exception;
    PlacesSearchResult[] findNearbyPlaces(LatLng location, int radius, String type) throws Exception;
}

package com.netcracker.datacollector.placecollector.service;

import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Class for searching places info using Google Maps API.
 *
 * @author Ali
 */
@Service
public class PlaceGoogleClient {

    @Value("${google.api.key}")
    private String apiKey;

    private final GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).maxRetries(10)
            .retryTimeout(20000, TimeUnit.MILLISECONDS).build();

    /**
     * Finds all places of specified type.
     *
     * @param type - type of searching place.
     * @param location - search location.
     * @return PlacesSearchResponse object that contains list of founded places and next page token.
     */
    public PlacesSearchResponse findAllPlacesByType(final String type, final LatLng location, final String token) {
        int searchRadius = 707; //Половина диагонали ячейки 1 на 1 км, для того, что бы охватить поиском всю ячейку.
        NearbySearchRequest request = PlacesApi.nearbySearchQuery(context, location)
                .radius(searchRadius)
                .language("ru")
                .type(PlaceType.valueOf(type));
        if(token != null) {
            return request.pageToken(token).awaitIgnoreError();
        }
        return request.awaitIgnoreError();
    }
}

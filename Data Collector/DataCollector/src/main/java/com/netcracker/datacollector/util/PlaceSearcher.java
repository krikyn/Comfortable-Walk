package com.netcracker.datacollector.util;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Class for searching places info using Google Maps API.
 *
 * @author Али
 */
@Component
public class PlaceSearcher{

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
     * @throws Exception
     */
    public PlacesSearchResponse findAllPlacesByType(final String type, final LatLng location) throws Exception {
        int searchRadius = 707;
        return PlacesApi.nearbySearchQuery(context, location)
                .radius(searchRadius)
                .language("ru")
                .type(PlaceType.valueOf(type))
                .awaitIgnoreError();
    }

    /**
     * Finds all places of specified type. Using when need find more than 20 places.
     *
     * @param type - type of searching place.
     * @param location - search location.
     * @param token - token for the next page of founded places.
     * @return PlacesSearchResponse object that contains list of founded places and next page token.
     * @throws Exception
     */
    public PlacesSearchResponse findAllPlacesByType(final String type, LatLng location, final String token) throws Exception {
        int searchRadius = 707;
        return PlacesApi.nearbySearchQuery(context, location)
                .radius(searchRadius)
                .language("ru")
                .type(PlaceType.valueOf(type))
                .pageToken(token)
                .awaitIgnoreError();
    }
}

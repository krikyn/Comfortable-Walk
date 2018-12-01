package com.netcracker.datacollector.util;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by Grout on 21.10.2018.
 *
 * Позволяет искать места используя Google Maps API.
 */

@Component
public class PlaceSearcher{

    @Value("${google.api.key}")
    private String apiKey;

    private final GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).maxRetries(10)
            .retryTimeout(20000, TimeUnit.MILLISECONDS).build();

    public PlacesSearchResponse findAllPlacesByType(final String type, final LatLng location) throws Exception {
        int searchRadius = 707;
        return PlacesApi.nearbySearchQuery(context, location)
                .radius(searchRadius)
                .language("ru")
                .type(PlaceType.valueOf(type))
                .awaitIgnoreError();
    }

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

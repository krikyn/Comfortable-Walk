package com.netcracker.datacollector.service.implementation;

import com.google.maps.FindPlaceFromTextRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import com.netcracker.datacollector.service.PlaceSearcher;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by Grout on 21.10.2018.
 */

@Component
public class PlaceSearcherImpl implements PlaceSearcher {

    private final LatLng SAINT_PETERSBURG = new LatLng(59.93428020,30.33509860);

    private final String apiKey = "AIzaSyD7x_qmEHJ6uDg7zbXBkSLeU3jLGRPaHrA";
    private final GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey)
            .queryRateLimit(1).retryTimeout(2, TimeUnit.SECONDS).build();

    public PlacesSearchResult findPlaceFromText(final String place) throws Exception {
        FindPlaceFromText response =
                PlacesApi.findPlaceFromText(context, place, FindPlaceFromTextRequest.InputType.TEXT_QUERY)
                .fields(FindPlaceFromTextRequest.FieldMask.PLACE_ID,
                        FindPlaceFromTextRequest.FieldMask.FORMATTED_ADDRESS,
                        FindPlaceFromTextRequest.FieldMask.NAME,
                        FindPlaceFromTextRequest.FieldMask.GEOMETRY)
                .locationBias(new FindPlaceFromTextRequest.LocationBiasIP())
                .await();

        PlacesSearchResult candidate = response.candidates[0];
        LatLng location = candidate.geometry.location;
        System.out.println("Place ID: " + candidate.placeId + "\n" +
                "Coordinates: " + location + "\n" +
                "name: " + candidate.name + "\n" +
                "address: " + candidate.formattedAddress);

        return candidate;
    }

    public PlaceDetails getPlaceDetails(final String placeID) throws Exception {
        return PlacesApi.placeDetails(context, placeID)
                .fields(
                        PlaceDetailsRequest.FieldMask.NAME,
                        PlaceDetailsRequest.FieldMask.TYPES,
                        PlaceDetailsRequest.FieldMask.FORMATTED_ADDRESS,
                        PlaceDetailsRequest.FieldMask.FORMATTED_PHONE_NUMBER,
                        PlaceDetailsRequest.FieldMask.GEOMETRY)
                .language("ru")
                .await();
    }

    public PlacesSearchResult[] findNearbyPlaces(final LatLng location, final int radius,
                                                 final String type) throws Exception {
        PlacesSearchResponse response = PlacesApi.nearbySearchQuery(context, location)
                .radius(radius)
                .language("ru")
                .type(PlaceType.valueOf(type))
                .await();

        return response.results;
    }

    public PlacesSearchResult[] findAllPlacesByType(final String type) throws Exception {
        PlacesSearchResponse response = PlacesApi.nearbySearchQuery(context, SAINT_PETERSBURG)
                .radius(18000)
                .language("ru")
                .type(PlaceType.valueOf(type))
                .await();

        return response.results;
    }
}

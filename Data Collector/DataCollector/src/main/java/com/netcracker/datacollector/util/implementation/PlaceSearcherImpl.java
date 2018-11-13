package com.netcracker.datacollector.util.implementation;

import com.google.maps.FindPlaceFromTextRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import com.netcracker.datacollector.util.PlaceSearcher;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by Grout on 21.10.2018.
 *
 * Класс реализующий интерфейс {@link PlaceSearcher}. Позволяет искать места используя Google Maps API.
 */

@Component
public class PlaceSearcherImpl implements PlaceSearcher {

    private final String apiKey = "AIzaSyBw3Bcepmq4q_VtqIohTNDBHPJnMiNw9yY";
    private final GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).maxRetries(10)
            .retryTimeout(20000, TimeUnit.MILLISECONDS).build();

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

    public PlacesSearchResponse findAllPlacesByType(final String type, final LatLng location) throws Exception {
        return PlacesApi.nearbySearchQuery(context, location)
                .radius(707)
                .language("ru")
                .type(PlaceType.valueOf(type))
                .awaitIgnoreError();
    }

    public PlacesSearchResponse findAllPlacesByType(final String type, LatLng location, final String token) throws Exception {
        return PlacesApi.nearbySearchQuery(context, location)
                .radius(707)
                .language("ru")
                .type(PlaceType.valueOf(type))
                .pageToken(token)
                .awaitIgnoreError();
    }
}

package com.netcracker.datacollector.util;

import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.netcracker.datacollector.data.model.Place;
import com.netcracker.datacollector.service.PlaceSearcherService;
import com.netcracker.datacollector.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Grout on 28.10.2018.
 */

@Component
public class ScheduledPlaceRecalculation {

    private final int MILLIS_PER_MINUTE = 60000;

    private final PlaceSearcherService searcher;
    private final PlaceService placeService;

    private int counter = 0;
    private SearchType[] searchType = SearchType.values();

    @Autowired
    public ScheduledPlaceRecalculation(PlaceSearcherService searcher, PlaceService placeService) {
        this.searcher = searcher;
        this.placeService = placeService;
    }

    @Scheduled(fixedDelay = 10 * MILLIS_PER_MINUTE)
    public void recalculatePlacesByType() throws Exception {
        if(counter > searchType.length) {
            counter = 0;
        }
        String placeType = searchType[counter].toString();
        List<PlacesSearchResult> resultAll= new ArrayList<>();
        PlacesSearchResponse resultResponse = searcher.findAllPlacesByType(placeType);
        PlacesSearchResponse resultResponse2;
        PlacesSearchResponse resultResponse3;
        Collections.addAll(resultAll, resultResponse.results);
        if(!resultResponse.nextPageToken.isEmpty()) { //Страшный костыль для сбора информации 60-ти объектов, а не только 20-ти
            Thread.sleep(20000);
            resultResponse2 = searcher.findAllPlacesByType(placeType, resultResponse.nextPageToken);
            if(resultResponse2 != null) { //Костыль intensifies
                Collections.addAll(resultAll, resultResponse2.results);
                if(resultResponse2.nextPageToken != null) { //Костыль intensifies x2
                    Thread.sleep(20000);
                    resultResponse3 = searcher.findAllPlacesByType(placeType, resultResponse.nextPageToken);
                    if(resultResponse3 != null) { //Костыль intensifies ULTRA COMBOOO
                        Collections.addAll(resultAll, resultResponse3.results);
                    }
                }
            }
        }
        savePlaceData(resultAll, placeType);
        System.out.println("Place data saved. Counter: " + counter);
        counter++;
    }

    private void savePlaceData(List<PlacesSearchResult> places, String type) {
        for(PlacesSearchResult place: places) {
            Place newPlace = new Place();

            newPlace.setName(place.name);
            newPlace.setType(type);
            newPlace.setGooglePlaceId(place.placeId);
            if(place.formattedAddress == null) {
                newPlace.setAddress(place.vicinity);
            } else {
                newPlace.setAddress(place.formattedAddress);
            }
            newPlace.setLatitude(place.geometry.location.lat);
            newPlace.setLongitude(place.geometry.location.lng);

            placeService.savePlace(newPlace);
        }
    }
}

package com.netcracker.datacollector.controller;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.netcracker.datacollector.data.model.Place;
import com.netcracker.datacollector.service.PlaceSearcherService;
import com.netcracker.datacollector.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/dataCollectorApi")
public class DataCollectorController {

    private final PlaceSearcherService searcher;

    private final PlaceService placeService;

    @Autowired
    public DataCollectorController(PlaceSearcherService searcher, PlaceService placeService) {
        this.searcher = searcher;
        this.placeService = placeService;
    }

    @GetMapping("/findPlace/{place}")
    public ResponseEntity<?> getPlaceFromText(@PathVariable String place) throws Exception {
        PlacesSearchResult result = searcher.findPlaceFromText(place);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/placeDetails/{placeID}")
    public ResponseEntity<?> getPlaceDetails(@PathVariable String placeID) throws Exception {
        PlaceDetails result = searcher.getPlaceDetails(placeID);
        System.out.println(result);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "/findNearbyPlaces", params = {"latlng", "radius", "type"})
    public ResponseEntity<?> getNearbyPlaces(@RequestParam("latlng") List<Double> coordinates,
                                             @RequestParam("radius") final int radius,
                                             @RequestParam("type") final String placeType) throws Exception {
        LatLng location = new LatLng(coordinates.get(0), coordinates.get(1));
        PlacesSearchResult[] result = searcher.findNearbyPlaces(location, radius, placeType);

        return ResponseEntity.ok().body(result);
    }

    /*@GetMapping(value = "/recalculatePlaces", params = "type")
    public ResponseEntity<?> recalculatePlacesByType(@RequestParam("type") final String placeType) throws Exception {
        List<PlacesSearchResult> resultAll= new ArrayList<>();
        PlacesSearchResponse resultResponse = searcher.findAllPlacesByType(placeType);
        Collections.addAll(resultAll, resultResponse.results);
        if(!resultResponse.nextPageToken.isEmpty()) { //Страшный костыль
            Thread.sleep(3000);
            resultResponse = searcher.findAllPlacesByType(placeType, resultResponse.nextPageToken);
            Collections.addAll(resultAll, resultResponse.results);
            if(resultResponse.nextPageToken != null) { //Костыль intensifies
                Thread.sleep(3000);
                resultResponse = searcher.findAllPlacesByType(placeType, resultResponse.nextPageToken);
                Collections.addAll(resultAll, resultResponse.results);
            }
        }

        savePlaceData(resultAll, placeType);
        *//*System.out.println("Размер: " + resultAll.size());
        System.out.println("Первый элемент: " + resultAll.get(0).geometry.location);
        System.out.println("Все элементы: " + resultAll);*//*

        return ResponseEntity.ok().body("Recalculated successfully");
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
    }*/
}

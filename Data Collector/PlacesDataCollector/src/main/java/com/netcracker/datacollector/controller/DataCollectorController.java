package com.netcracker.datacollector.controller;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;
import com.netcracker.datacollector.service.PlaceSearcher;
import com.netcracker.datacollector.util.SearchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/dataCollectorApi")
public class DataCollectorController {

    private final PlaceSearcher searcher;

    @Autowired
    public DataCollectorController(PlaceSearcher searcher) {
        this.searcher = searcher;
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

    @GetMapping(value = "/recalculatePlaces", params = "type")
    public ResponseEntity<?> recalculatePlacesByType(@RequestParam("type") final String placeType) throws Exception {
        List<PlacesSearchResult> resultAll= new ArrayList<>();

        Collections.addAll(resultAll, searcher.findAllPlacesByType(placeType));

        System.out.println("Размер: " + resultAll.size());
        System.out.println("Первый элемент: " + resultAll.get(0).geometry.location);
        System.out.println("Все элементы: " + resultAll);
        return ResponseEntity.ok().body(resultAll);
    }
}

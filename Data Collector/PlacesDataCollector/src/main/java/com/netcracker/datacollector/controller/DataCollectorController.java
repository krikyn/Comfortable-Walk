package com.netcracker.datacollector.controller;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;
import com.netcracker.datacollector.data.model.CityMap;
import com.netcracker.datacollector.service.CityMapService;
import com.netcracker.datacollector.util.PlaceSearcher;
import com.netcracker.datacollector.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dataCollectorApi")
public class DataCollectorController {

    private final PlaceSearcher searcher;
    private final PlaceService placeService;
    private final CityMapService cityMapService;

    @Autowired
    public DataCollectorController(PlaceSearcher searcher, PlaceService placeService, CityMapService cityMapService) {
        this.searcher = searcher;
        this.placeService = placeService;
        this.cityMapService = cityMapService;
    }

    /*@GetMapping("/findPlace/{place}")
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

    @GetMapping("/baseMap")
    public ResponseEntity<?> getBaseMap() {
        CityMap map = loadMapByType("baseCityMap1km");
        return ResponseEntity.ok().body(map.getBaseMap());
    }

    @GetMapping(value = "/placeMap", params = "type")
    public ResponseEntity<?> getPlaceMap(@RequestParam("type") String type) {
        CityMap map = loadMapByType(type);

        System.out.println();
        System.out.println();
        System.out.println("Map: " + type);
        for(int i = 0; i < 21; i++) {
            for(int j = 0; j < 20; j++) {
                System.out.printf("%5s", "-----");
            }
            System.out.print('\n');
            for (int j = 0; j < 20; j++) {
                System.out.printf("%5s", map.getGrid()[i][j] + "|");
            }
            System.out.print('\n');
        }

        return ResponseEntity.ok().body(map.getGrid());
    }*/

    private CityMap loadMapByType(String type) {
        return cityMapService.loadCityMapByType(type);
    }
}

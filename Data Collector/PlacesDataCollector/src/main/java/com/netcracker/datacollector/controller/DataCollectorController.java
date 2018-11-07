package com.netcracker.datacollector.controller;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;
import com.netcracker.datacollector.data.model.CityMap;
import com.netcracker.datacollector.service.CityMapService;
import com.netcracker.datacollector.util.MapBuilder;
import com.netcracker.datacollector.util.PlaceSearcher;
import com.netcracker.datacollector.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

<<<<<<< HEAD
=======
import java.util.List;
import java.util.Optional;

>>>>>>> f7f81ff2ab86f41a313299e483995949aba8494e
@RestController
@RequestMapping("/placesApi")
public class DataCollectorController {

    private final PlaceSearcher searcher;
    private final PlaceService placeService;
    private final CityMapService cityMapService;
    private final MapBuilder mapBuilder;

    @Autowired
    public DataCollectorController(PlaceSearcher searcher, PlaceService placeService, CityMapService cityMapService, MapBuilder mapBuilder) {
        this.searcher = searcher;
        this.placeService = placeService;
        this.cityMapService = cityMapService;
<<<<<<< HEAD
        this.mapBuilder = mapBuilder;
=======
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
>>>>>>> f7f81ff2ab86f41a313299e483995949aba8494e
    }

    @GetMapping(value = "/placeMap", params = "type")
    public ResponseEntity<?> getPotentialPlaceMap(@RequestParam("type") String type) {
        CityMap map = loadMapByType(type);
        int [][] placeMap = mapBuilder.buildPotentialMap(map.getGrid(), 20);
        CityMap savedMap = savePotentialMap(placeMap, type);
        return ResponseEntity.ok().body(savedMap.getGrid());
    }

    @GetMapping("/amusement")
    public ResponseEntity<?> getAmusement() {
        CityMap map = loadMapByType("POTENTIAL_AMUSEMENT_PARK");
        System.out.println("--------------------------------");
        for(int i = 0; i < 420; i++) {
            for(int j = 0; j < 400; j++){
                System.out.printf("%5d", map.getGrid()[i][j]);
            }
            System.out.println();
        }
        return ResponseEntity.ok().body(map.getGrid());
    }*/

    private CityMap loadMapByType(String type) {
        return cityMapService.loadCityMapByType(type);
    }

    private CityMap savePotentialMap(int[][] placeMap, String type) {
        CityMap map = new CityMap();
        map.setGrid(placeMap);
        map.setType("POTENTIAL_" + type);
        return cityMapService.saveMap(map);
    }
}

package com.netcracker.datacollector.controller;

import com.netcracker.datacollector.data.model.CityMap;
import com.netcracker.datacollector.service.CityMapService;
import com.netcracker.datacollector.util.MapBuilder;
import com.netcracker.datacollector.util.PlaceSearcher;
import com.netcracker.datacollector.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/placesApi")
public class PlaceCollectorController {

    private final PlaceSearcher searcher;
    private final PlaceService placeService;
    private final CityMapService cityMapService;
    private final MapBuilder mapBuilder;

    @Autowired
    public PlaceCollectorController(PlaceSearcher searcher, PlaceService placeService, CityMapService cityMapService, MapBuilder mapBuilder) {
        this.searcher = searcher;
        this.placeService = placeService;
        this.cityMapService = cityMapService;
        this.mapBuilder = mapBuilder;
    }

    /*@GetMapping("/baseMap50m") Для дебага
    public ResponseEntity<?> getBaseMap50m() {
        CityMap map = loadMapByType("baseCityMap50m");
        return ResponseEntity.ok().body(map.getBaseMap());
    }*/

    @GetMapping(value = "/placeMap", params = "type")
    public ResponseEntity<?> getPotentialPlaceMap(@RequestParam("type") String type) {
        CityMap map = loadMapByType(type);
        return ResponseEntity.ok().body(map.getGrid());
    }

    //Для дебага
    @GetMapping("/amusement")
    public ResponseEntity<?> getAmusement() {
        CityMap placeMap = loadMapByType("POTENTIAL_AMUSEMENT_PARK");
        System.out.println("--------------------------------");
        for(int i = 0; i < 420; i++) {
            for(int j = 0; j < 400; j++){
                System.out.printf("%5d", placeMap.getGrid()[i][j]);
            }
            System.out.println();
        }
        return ResponseEntity.ok().body(placeMap.getGrid());
    }

    private CityMap loadMapByType(String type) {
        return cityMapService.loadCityMapByType(type);
    }

    /*private CityMap savePotentialMap(int[][] placeMap, String type) {
        CityMap map = new CityMap();
        map.setGrid(placeMap);
        map.setType("POTENTIAL_" + type);
        return cityMapService.saveMap(map);
    }*/
}

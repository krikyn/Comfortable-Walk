package com.netcracker.datacollector.controller;

import com.netcracker.datacollector.data.model.CityMap;
import com.netcracker.datacollector.service.CityMapService;
import com.netcracker.datacollector.util.MapBuilder;
import com.netcracker.datacollector.util.PlaceSearcher;
import com.netcracker.datacollector.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Grout on 28.10.2018.
 *
 * Контроллер для работы с потенциальными картами.
 * */

@RestController
@RequestMapping("/placesApi")
public class PlaceCollectorController {

    private final CityMapService cityMapService;

    @Autowired
    public PlaceCollectorController(CityMapService cityMapService) {
        this.cityMapService = cityMapService;
    }

    /*@GetMapping("/baseMap50m") Для дебага
    public ResponseEntity<?> getBaseMap50m() {
        CityMap map = loadMapByType("baseCityMap50m");
        return ResponseEntity.ok().body(map.getBaseMap());
    }*/

    /**
     * Загружает и выдаёт потенциальную карту места.
     * @param type - тип запрашиваемой потенциальной карты(тип должен быть вида "POTENTIAL_PARK").
     * */
    @GetMapping(value = "/placeMap", params = "type")
    public ResponseEntity<?> getPotentialPlaceMap(@RequestParam("type") String type) {
        CityMap map = loadMapByType(type.toUpperCase());
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
}

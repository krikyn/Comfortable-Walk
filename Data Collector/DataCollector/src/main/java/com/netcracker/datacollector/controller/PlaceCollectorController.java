package com.netcracker.datacollector.controller;

import com.netcracker.datacollector.data.model.CityMap;
import com.netcracker.datacollector.service.CityMapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Grout on 28.10.2018.
 *
 * Контроллер для работы с потенциальными картами.
 * */

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class PlaceCollectorController {

    private final CityMapService cityMapService;

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
        CityMap map = cityMapService.loadCityMapByType(type);
        return ResponseEntity.ok().body(map.getBaseMap());
    }

    //Для дебага
    @GetMapping("/amusement")
    public ResponseEntity<?> getAmusement() {
        int maxRow = 420;
        int maxCol = 400;
        CityMap placeMap = cityMapService.loadCityMapByType("POTENTIAL_AMUSEMENT_PARK");
        log.debug("Amusement park potential map.");
        for(int i = 0; i < maxRow; i++) {
            for(int j = 0; j < maxCol; j++){
                log.debug("%5d", placeMap.getGrid()[i][j]);
            }
        }
        return ResponseEntity.ok().body(placeMap.getGrid());
    }
}

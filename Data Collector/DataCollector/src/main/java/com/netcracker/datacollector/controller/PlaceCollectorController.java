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
// daba Кусок адреса api - это по умолчанию API, писать в название не обязательно
@RequestMapping("/placesApi")
public class PlaceCollectorController {

    private final CityMapService cityMapService;

    // daba Можно через Lombok
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
        // daba Lombok + @Slf4J + log.debug() вместо println
        System.out.println("--------------------------------");
        // daba Магические цифры
        for(int i = 0; i < 420; i++) {
            for(int j = 0; j < 400; j++){
                System.out.printf("%5d", placeMap.getGrid()[i][j]);
            }
            System.out.println();
        }
        return ResponseEntity.ok().body(placeMap.getGrid());
    }

    // daba Смысл метода из одной строки?
    private CityMap loadMapByType(String type) {
        return cityMapService.loadCityMapByType(type);
    }
}

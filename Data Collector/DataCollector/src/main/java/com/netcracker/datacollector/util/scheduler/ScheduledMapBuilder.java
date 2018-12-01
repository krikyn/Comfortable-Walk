package com.netcracker.datacollector.util.scheduler;

import com.netcracker.commons.data.model.CityMap;
import com.netcracker.commons.data.model.Place;
import com.netcracker.commons.service.CityMapService;
import com.netcracker.commons.service.PlaceService;
import com.netcracker.datacollector.util.MapBuilder;
import com.netcracker.datacollector.util.enums.PlacesType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class for automatic map building in specified intervals of time (by default once in 1 hour).
 *
 * @author Али
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScheduledMapBuilder {

    private final int MILLIS_PER_MINUTE = 60000;

    private final MapBuilder mapBuilder;
    private final CityMapService cityMapService;
    private final PlaceService placeService;

    /**
     * Builds maps at specified intervals (by default once in 1 hour).
     */
    @Scheduled(fixedDelay = MILLIS_PER_MINUTE * 60)
    public void buildMaps() {
        //Строит и загружает базовые карты размером 1 на 1 км и 50 на 50 м
        if(cityMapService.loadCityMapByType("baseCityMap1km") == null) {
            CityMap baseCityMap = new CityMap();
            baseCityMap.setType("baseCityMap1km");
            baseCityMap.setBaseMap(mapBuilder.buildBaseMap(1));
            cityMapService.saveMap(baseCityMap);
        }
        if(cityMapService.loadCityMapByType("baseCityMap50m") == null) {
            CityMap baseCityMap = new CityMap();
            baseCityMap.setType("baseCityMap50m");
            baseCityMap.setBaseMap(mapBuilder.buildBaseMap(20));
            cityMapService.saveMap(baseCityMap);
        }

        for(PlacesType place: PlacesType.values()) {
            List<Place> places = placeService.loadAllPlacesByType(place.toString());
            CityMap map = new CityMap();
            CityMap loadedMap = cityMapService.loadCityMapByType("POTENTIAL_" + place.toString());
            if(loadedMap == null && places != null) {
                int[][] placeMap = mapBuilder.buildPlaceMap(places, 20); //Построение карты мест
                map.setType("POTENTIAL_" + place.toString());
                map.setGrid(mapBuilder.buildPotentialMap(placeMap, 20)); //Построение и установка потенциальной карты мест
                cityMapService.saveMap(map);
            } else if(loadedMap != null) {
                int[][] placeMap = mapBuilder.buildPlaceMap(places, 20);
                map.setId(loadedMap.getId());
                map.setType(loadedMap.getType());
                map.setGrid(mapBuilder.buildPotentialMap(placeMap, 20));
                cityMapService.saveMap(map);
            }
        }
    }
}

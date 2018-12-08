package com.netcracker.datacollector.mapcollector.scheduler;

import com.netcracker.commons.data.model.CityMap;
import com.netcracker.commons.data.model.Place;
import com.netcracker.commons.service.CityMapService;
import com.netcracker.commons.service.PlaceService;
import com.netcracker.datacollector.mapcollector.maputil.MapBuilder;
import com.netcracker.datacollector.util.enums.PlacesType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class for automatic map building in specified intervals of time (by default once in 1 hour).
 *
 * @author Ali
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScheduledMapBuilder {

    private final int MILLIS_PER_MINUTE = 60000;

    private final MapBuilder mapBuilder;
    private final CityMapService cityMapService;
    private final PlaceService placeService;

    /**
     * Builds maps at specified intervals of time (by default once in 1 hour).
     */
    @Scheduled(fixedDelay = MILLIS_PER_MINUTE * 60)
    public void buildMaps() {
        int scaleFor50m = 20;
        //Строит и загружает базовые карты размером 1 на 1 км
        if(cityMapService.loadCityMapByType("baseCityMap1km") == null) {
            CityMap baseCityMap = new CityMap();
            baseCityMap.setType("baseCityMap1km");
            baseCityMap.setBaseMap(mapBuilder.buildBaseMap(1));
            cityMapService.saveMap(baseCityMap);
        }

        for(PlacesType place: PlacesType.values()) {
            String mapName = "POTENTIAL_" + place.toString();
            List<Place> places = placeService.loadAllPlacesByType(place.toString());
            CityMap map = new CityMap();
            CityMap loadedMap = cityMapService.loadCityMapByType(mapName);
            if(loadedMap == null && places != null) {
                int[][] placeMap = mapBuilder.buildPlaceMap(places, scaleFor50m); //Построение карты мест
                map.setType(mapName);
                map.setGrid(mapBuilder.buildPotentialMap(placeMap, scaleFor50m)); //Построение и установка потенциальной карты мест
                cityMapService.saveMap(map);
            } else if(loadedMap != null) {
                int[][] placeMap = mapBuilder.buildPlaceMap(places, scaleFor50m);
                map.setId(loadedMap.getId());
                map.setType(loadedMap.getType());
                map.setGrid(mapBuilder.buildPotentialMap(placeMap, scaleFor50m));
                cityMapService.saveMap(map);
            }
        }
    }
}

package com.netcracker.datacollector.util.scheduler;

import com.netcracker.datacollector.data.model.CityMap;
import com.netcracker.datacollector.data.model.Place;
import com.netcracker.datacollector.service.CityMapService;
import com.netcracker.datacollector.service.PlaceService;
import com.netcracker.datacollector.util.MapBuilder;
import com.netcracker.datacollector.util.enums.PlacesType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledMapBuilder {

    private final int MILLIS_PER_MINUTE = 60000;

    private final MapBuilder mapBuilder;
    private final CityMapService cityMapService;
    private final PlaceService placeService;

    @Autowired
    public ScheduledMapBuilder(MapBuilder mapBuilder, CityMapService cityMapService, PlaceService placeService) {
        this.mapBuilder = mapBuilder;
        this.cityMapService = cityMapService;
        this.placeService = placeService;
    }

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
            CityMap baseMap = cityMapService.loadCityMapByType("baseCityMap50m");
            List<Place> places = placeService.loadAllPlacesByType(place.toString());
            CityMap map = new CityMap();
            if(places != null) {
                int[][] placeMap = mapBuilder.buildPlaceMap(baseMap.getBaseMap(), places, 20); //Построение карты мест
                map.setType("POTENTIAL_" + place.toString());
                map.setGrid(mapBuilder.buildPotentialMap(placeMap, 20)); //Построение и установка потенциальной карты мест
                cityMapService.saveMap(map);
            }
        }
    }
}

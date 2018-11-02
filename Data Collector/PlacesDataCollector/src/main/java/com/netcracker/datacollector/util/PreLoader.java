package com.netcracker.datacollector.util;

import com.netcracker.datacollector.data.model.CityMap;
import com.netcracker.datacollector.data.model.Place;
import com.netcracker.datacollector.service.CityMapService;
import com.netcracker.datacollector.service.PlaceService;
import com.netcracker.datacollector.util.enums.PlacesType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PreLoader {

    private final MapBuilder mapBuilder;

    @Autowired
    public PreLoader(MapBuilder mapBuilder) {
        this.mapBuilder = mapBuilder;
    }

    @Bean
    CommandLineRunner initBaseMap(CityMapService cityMapService, PlaceService placeService) {
        return args -> {
            if(cityMapService.loadCityMapByType("baseCityMap1km") == null) {
                CityMap baseCityMap = new CityMap();
                baseCityMap.setType("baseCityMap1km");
                baseCityMap.setBaseMap(mapBuilder.buildBaseMap());
                cityMapService.saveMap(baseCityMap);
            }
            /*CityMap baseMap = cityMapService.loadCityMapByType("baseCityMap");

            PlacesType[] placesTypes = PlacesType.values();
            for (int i = 0; i < placesTypes.length-1; i++) {
                List<Place> places = placeService.loadAllPlacesByType(placesTypes[i].toString());
                if(cityMapService.loadCityMapByType(placesTypes[i].toString()) == null) {
                    CityMap map = new CityMap();
                    map.setType(placesTypes[i].toString());
                    map.setGrid(mapBuilder.buildPlaceMap(baseMap.getBaseMap(), places, 1)); //Строится карта мест на основе базовой карты
                    cityMapService.saveMap(map);
                }
            }*/
        };
    }
}

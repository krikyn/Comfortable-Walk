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

            //Строит и загружает потенциальную карту места
            PlacesType[] placesType = PlacesType.values();
            CityMap baseMap = cityMapService.loadCityMapByType("baseCityMap50m");
            /*for (PlacesType place: placesType) {
                List<Place> places = placeService.loadAllPlacesByType(place.toString());
                if(cityMapService.loadCityMapByType(place.toString()) == null) {
                    CityMap map = new CityMap();
                    int[][] result = mapBuilder.buildPlaceMap(baseMap.getBaseMap(), places, 20);
                    map.setType(place.toString());
                    map.setGrid(result);
                    cityMapService.saveMap(map);
                    System.out.println("--------------------------------");
                    for(int i = 0; i < 420; i++) {
                        for(int j = 0; j < 400; j++){
                            System.out.printf("%5d", result[i][j]);
                        }
                        System.out.println();
                    }
                }
            }*/
        };
    }
}

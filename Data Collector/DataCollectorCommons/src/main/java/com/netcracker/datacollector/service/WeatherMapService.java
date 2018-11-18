package com.netcracker.datacollector.service;

import com.netcracker.datacollector.data.model.WeatherPotentialMap;
import org.springframework.stereotype.Component;

@Component
public interface WeatherMapService {

    WeatherPotentialMap getMap();
    void loadMap(WeatherPotentialMap map);
    void updateMap(WeatherPotentialMap map);
}

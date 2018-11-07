package com.netcracker.weatherdatacollector.service;

import com.netcracker.weatherdatacollector.entity.WeatherPotentialMap;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface WeatherMapService {

    WeatherPotentialMap getMap();
    void loadMap(WeatherPotentialMap map);
    void updateMap(WeatherPotentialMap map);
}

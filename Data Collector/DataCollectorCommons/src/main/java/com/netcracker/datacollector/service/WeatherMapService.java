package com.netcracker.datacollector.service;

import com.netcracker.datacollector.data.model.WeatherPotentialMap;
import com.netcracker.datacollector.data.repository.WeatherPotentialMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class WeatherMapService {

    private final WeatherPotentialMapRepository repository;
    private final static int MAP_ID_IN_BD = 0;

    @Transactional
    public WeatherPotentialMap getMap() {
        return repository.findCityMapById(MAP_ID_IN_BD);
    }
}

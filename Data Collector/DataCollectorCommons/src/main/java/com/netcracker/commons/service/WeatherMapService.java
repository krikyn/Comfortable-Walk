package com.netcracker.commons.service;

import com.netcracker.commons.data.model.WeatherPotentialMap;
import com.netcracker.commons.data.repository.WeatherPotentialMapRepository;
import lombok.RequiredArgsConstructor;
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

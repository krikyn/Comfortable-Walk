package com.netcracker.weatherdatacollector.service.implementation;

import com.netcracker.weatherdatacollector.entity.WeatherPotentialMap;
import com.netcracker.weatherdatacollector.repository.WeatherPotentialMapRepository;
import com.netcracker.weatherdatacollector.service.WeatherMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeatherMapServiceImpl implements WeatherMapService {

    private final WeatherPotentialMapRepository repository;

    @Autowired
    public WeatherMapServiceImpl(WeatherPotentialMapRepository repository) {
        this.repository = repository;
    }

    @Override
    public WeatherPotentialMap getMap() {
        return repository.getOne(0);
    }

    @Override
    public void loadMap(WeatherPotentialMap map) {
        repository.saveAndFlush(map);
    }

    @Override
    public void updateMap(WeatherPotentialMap map) {

        if (!repository.existsById(0)){
            loadMap(map);
        } else {
            repository.deleteById(0);
            repository.saveAndFlush(map);
        }


        /*
        WeatherPotentialMap weatherPotentialMap = repository.getOne(0);

        if (weatherPotentialMap == null) {
            loadMap(map);
        } else {
            weatherPotentialMap.setPotentialField(map.getPotentialField());
            repository.saveAndFlush(weatherPotentialMap);
        }
        */

        //repository.updateMap(0, map.getPotentialField());

    }
}

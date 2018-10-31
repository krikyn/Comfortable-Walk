package com.netcracker.datacollector.service.implementation;

import com.netcracker.datacollector.data.model.CityMap;
import com.netcracker.datacollector.data.repository.CityMapRepository;
import com.netcracker.datacollector.service.CityMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class CityMapServiceImpl implements CityMapService {

    private final CityMapRepository repository;


    @Autowired
    public CityMapServiceImpl(CityMapRepository repository) {
        this.repository = repository;
    }

    @Override
    public CityMap saveMap(CityMap cityMap) {
        return repository.saveAndFlush(cityMap);
    }

    @Override
    @Transactional
    public CityMap loadCityMapByType(String type) {
        return repository.findCityMapByType(type);
    }
}

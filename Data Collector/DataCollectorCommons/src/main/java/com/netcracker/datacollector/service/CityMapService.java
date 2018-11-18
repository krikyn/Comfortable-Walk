package com.netcracker.datacollector.service;

import com.netcracker.datacollector.data.model.CityMap;
import com.netcracker.datacollector.data.repository.CityMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CityMapService {

    private final CityMapRepository repository;


    public void saveMap(CityMap cityMap) {
        repository.save(cityMap);
        repository.flush();
    }

    @Transactional
    public CityMap loadCityMapByType(String type) {
        return repository.findCityMapByType(type);
    }
}

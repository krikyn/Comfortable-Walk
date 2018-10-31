package com.netcracker.datacollector.service;

import com.netcracker.datacollector.data.model.CityMap;

public interface CityMapService {

    CityMap saveMap(CityMap cityMap);
    CityMap loadCityMapByType(String type);
}

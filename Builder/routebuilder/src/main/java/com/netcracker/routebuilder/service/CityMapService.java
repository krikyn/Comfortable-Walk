package com.netcracker.routebuilder.service;

import com.netcracker.routebuilder.data.model.CityMap;

public interface CityMapService {
    CityMap loadCityMapByType(String type);
}

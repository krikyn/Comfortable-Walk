package com.netcracker.datacollector.data.repository;

import com.netcracker.datacollector.data.model.CityMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CityMapRepository extends JpaRepository<CityMap, UUID> {

    CityMap findCityMapByType(String type);
}

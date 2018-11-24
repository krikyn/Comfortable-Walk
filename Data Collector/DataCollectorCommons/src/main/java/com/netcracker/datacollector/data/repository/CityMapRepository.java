package com.netcracker.datacollector.data.repository;

import com.netcracker.datacollector.data.model.CityMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CityMapRepository extends JpaRepository<CityMap, UUID> {

    CityMap findCityMapByType(String type);
}

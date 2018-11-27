package com.netcracker.datacollector.data.repository;

import com.netcracker.datacollector.data.model.CityMap;
import com.netcracker.datacollector.data.model.WeatherPotentialMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WeatherPotentialMapRepository extends JpaRepository<WeatherPotentialMap, Integer> {

    @Transactional
    @Modifying
    @Query("UPDATE WeatherPotentialMap m SET m.potentialField = :map WHERE m.id = :mapId")
    void updateAddress(@Param("map") int[][] map, @Param("mapId") int mapId);

    WeatherPotentialMap findCityMapById(int id);
}
package com.netcracker.datacollector.data.repository;

import com.netcracker.datacollector.data.model.WeatherPotentialMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WeatherPotentialMapRepository extends JpaRepository<WeatherPotentialMap, Integer> {

    /*
    @Modifying(clearAutomatically = true)
    @Query("Update WeatherPotentialMap wMap SET wMap.potentialField=:potentialField WHERE wMap.id=:id")
    public void updateMap(@Param("id") int id, @Param("potentialField") int[][] potentialField);
    */
}

package com.netcracker.commons.data.repository;

import com.netcracker.commons.data.model.WeatherPotentialMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Weather potential map repository
 *
 * @author Kirill.Vakhrushev
 */
@Repository
public interface WeatherPotentialMapRepository extends JpaRepository<WeatherPotentialMap, Integer> {

    /**
     * Update weather map in DB
     *
     * @param map   potential map
     * @param mapId map id
     */
    @Transactional
    @Modifying
    @Query("UPDATE WeatherPotentialMap m SET m.potentialField = :map WHERE m.id = :mapId")
    void updateAddress(@Param("map") int[][] map, @Param("mapId") int mapId);

    WeatherPotentialMap findCityMapById(int id);
}
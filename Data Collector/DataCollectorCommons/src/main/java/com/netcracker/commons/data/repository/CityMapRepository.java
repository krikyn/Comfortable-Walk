package com.netcracker.commons.data.repository;

import com.netcracker.commons.data.model.CityMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for {@link CityMap}
 *
 * @author Ali
 */
@Repository
public interface CityMapRepository extends JpaRepository<CityMap, UUID> {

    CityMap findCityMapByType(String type);
}

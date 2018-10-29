package com.netcracker.datacollector.data.repository;

import com.netcracker.datacollector.data.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Grout on 28.10.2018.
 */
public interface PlaceRepository extends JpaRepository<Place, UUID> {

    Optional<Place> findByName(String name);

}

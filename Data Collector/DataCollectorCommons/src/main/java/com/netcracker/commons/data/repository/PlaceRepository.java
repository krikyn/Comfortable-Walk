package com.netcracker.commons.data.repository;

import com.netcracker.commons.data.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link Place}
 *
 * @author Ali
 */
@Repository
public interface PlaceRepository extends JpaRepository<Place, UUID> {

    List<Place> findAllByType(String type);
}

package com.netcracker.commons.data.repository;

import com.netcracker.commons.data.model.Distance;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Distance entity
 * @author prokhorovartem
 */
public interface DistanceRepository extends JpaRepository<Distance, Long> {
}

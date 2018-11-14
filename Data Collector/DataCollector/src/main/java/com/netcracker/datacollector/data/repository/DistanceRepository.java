package com.netcracker.datacollector.data.repository;

import com.netcracker.datacollector.data.model.Distance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistanceRepository extends JpaRepository<Distance, Long> {
}

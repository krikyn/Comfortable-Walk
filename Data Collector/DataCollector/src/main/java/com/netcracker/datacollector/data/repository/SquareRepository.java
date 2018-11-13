package com.netcracker.datacollector.data.repository;

import com.netcracker.datacollector.data.model.Square;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SquareRepository extends JpaRepository<Square, Long> {
}

package com.netcracker.distancecollector.data.repository;

import com.netcracker.distancecollector.data.model.Square;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SquareRepository extends JpaRepository<Square, Long> {
}

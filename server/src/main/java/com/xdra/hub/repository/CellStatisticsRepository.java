package com.xdra.hub.repository;

import com.xdra.hub.entity.CellStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CellStatisticsRepository extends JpaRepository<CellStatisticsEntity, Long> {
}

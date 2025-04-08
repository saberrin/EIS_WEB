package com.xdra.hub.repository;

import com.xdra.hub.entity.PackStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackStatisticsRepository extends JpaRepository<PackStatisticsEntity, Long> {
}

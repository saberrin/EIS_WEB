package com.xdra.hub.repository;

import com.xdra.hub.analytics.StatsDto;
import com.xdra.hub.entity.EisMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EisMeasurementRepository extends JpaRepository<EisMeasurementEntity, Long> {
    @Query(value = "select new com.xdra.hub.analytics.StatsDto(count(distinct m.creationTime) as totalInspections, count(1) as totalMeasurements) from EisMeasurementEntity m where m.creationTime >= ?1 and m.creationTime < ?2")
    StatsDto getStatsByCreationTimeBetween(Instant start, Instant end);

    List<EisMeasurementEntity> findByCreationTimeBetween(Instant start, Instant end);

    @Query(value = "select * from eis_measurement order by creation_time desc limit 1", nativeQuery = true)
    Optional<EisMeasurementEntity> getLatestRecord();
}

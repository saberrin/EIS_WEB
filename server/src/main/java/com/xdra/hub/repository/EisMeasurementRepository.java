package com.xdra.hub.repository;

import com.xdra.hub.analytics.StatsDto;
import com.xdra.hub.entity.EisMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface EisMeasurementRepository extends JpaRepository<EisMeasurementEntity, Long> {
    @Query(value = "SELECT id,container_id,cluster_id,a.pack_id,cell_id,real_impedance,imaginary_impedance,temperature,frequency,voltage,creation_time " +
            "FROM eis_measurement a " +
            "inner join (SELECT pack_id,MAX(creation_time) as max_creation_time FROM eis_measurement where pack_id in ?1 and creation_time > ?2 group by pack_id) b " +
            "ON a.pack_id=b.pack_id and a.creation_time = b.max_creation_time", nativeQuery = true)
    List<EisMeasurementEntity> getLatestRecordsGroupByPackId(List<Long> packIds, Instant createdAfter);

    @Query(value = "select * from eis_measurement where pack_id = ?1 and creation_time = " +
            "(select creation_time from eis_measurement where pack_id = ?1 and creation_time > ?2 order by creation_time desc limit 1)", nativeQuery = true)
    List<EisMeasurementEntity> getPackLatestRecords(long packId, Instant createdAfter);

    @Query(value = "select new com.xdra.hub.analytics.StatsDto(count(distinct m.creationTime) as totalInspections, count(1) as totalMeasurements) from EisMeasurementEntity m where m.creationTime >= ?1 and m.creationTime < ?2")
    StatsDto getStatsByCreationTimeBetween(Instant start, Instant end);
}

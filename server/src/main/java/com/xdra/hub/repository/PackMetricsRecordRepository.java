package com.xdra.hub.repository;

import com.xdra.hub.entity.PackMetricsRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackMetricsRecordRepository extends JpaRepository<PackMetricsRecordEntity, Long> {
    @Query(value = "SELECT id,container_id,cluster_id,a.pack_id,dispersion_coefficient,safety_rate,creation_time " +
            "FROM pack_metrics_record a " +
            "inner join (SELECT pack_id,MAX(creation_time) as max_creation_time FROM pack_metrics_record where container_id=?1 and cluster_id=?2 group by pack_id) b " +
            "ON a.pack_id=b.pack_id and a.creation_time = b.max_creation_time", nativeQuery = true)
    List<PackMetricsRecordEntity> getLatestRecordsGroupByPackId(long containerId, long clusterId);

    @Query(value = "select * from pack_metrics_record where pack_id = :packId order by creation_time desc limit :limit", nativeQuery = true)
    List<PackMetricsRecordEntity> getPackLatestNRecords(@Param("packId") long packId, @Param("limit") int limit);
}

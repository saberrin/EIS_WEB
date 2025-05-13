package com.xdra.hub.repository;

import com.xdra.hub.entity.PackStatisticsEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackStatisticsRepository extends JpaRepository<PackStatisticsEntity, Long> {

    @Query(value = "select alert_text from pack_statistics where alert_text is not null order by creation_time desc limit 50", nativeQuery = true)
    List<String> getAlertTexts();

    @Query(value = "SELECT * FROM (SELECT ps.*, ROW_NUMBER() OVER (PARTITION BY ps.pack_id ORDER BY ps.creation_time DESC) as rn FROM pack_statistics ps WHERE ps.container_id = ?1) ranked WHERE ranked.rn <= 10", nativeQuery = true)
    List<PackStatisticsEntity> get10LatestPackStatisticsGroupByPackIdByContainerId(Long containerId);

    List<PackStatisticsEntity> findTopNByPackIdOrderByCreationTimeDesc(Long packId, Limit limit);
}

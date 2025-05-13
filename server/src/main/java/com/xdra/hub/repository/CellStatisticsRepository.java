package com.xdra.hub.repository;

import com.xdra.hub.entity.CellStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CellStatisticsRepository extends JpaRepository<CellStatisticsEntity, Long> {

    @Query("SELECT c FROM CellStatisticsEntity c WHERE c.packId = :packId AND c.cellId = :cellId AND c.creationTime BETWEEN :dateFrom AND :dateTo ORDER BY c.creationTime DESC")
    List<CellStatisticsEntity> findLatestByPackIdAndCellIdBetweenDates(@Param("packId") Long packId, @Param("cellId") Long cellId, @Param("dateFrom") Instant dateFrom, @Param("dateTo") Instant dateTo);
}

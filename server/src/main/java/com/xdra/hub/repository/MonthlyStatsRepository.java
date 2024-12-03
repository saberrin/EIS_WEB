package com.xdra.hub.repository;

import com.xdra.hub.analytics.StatsDto;
import com.xdra.hub.entity.MonthlyStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyStatsRepository extends JpaRepository<MonthlyStatsEntity, Long> {

    @Modifying
    @Query(value = "insert into monthly_statistics (year_month, total_inspections, total_measurements) values (:#{#stats.yearMonth}, :#{#stats.totalInspections}, :#{#stats.totalMeasurements}) on conflict on constraint monthly_statistics_year_month_key do nothing", nativeQuery = true)
    void upsert(MonthlyStatsEntity stats);

    boolean existsByYearMonth(String yearMonth);

    @Query(value = "select new com.xdra.hub.analytics.StatsDto(sum(s.totalInspections) as totalInspections, sum(s.totalMeasurements) as totalMeasurements) from MonthlyStatsEntity s")
    StatsDto getStats();
}

package com.xdra.hub.analytics;

import com.xdra.hub.entity.MonthlyStatsEntity;
import com.xdra.hub.model.Overview;
import com.xdra.hub.repository.EisMeasurementRepository;
import com.xdra.hub.repository.MonthlyStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final MonthlyStatsRepository monthlyStatsRepository;
    private final EisMeasurementRepository eisMeasurementRepository;

    public void calculateMonthlyStats() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        ZonedDateTime currentMonthStart = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
        ZonedDateTime lastMonthStart = currentMonthStart.minusMonths(1);

        String yearMonth = formatter.format(lastMonthStart);
        log.info("calculating monthly statistics triggered for {}", yearMonth);

        // calculate stats of last month, only need to do once
        if (!monthlyStatsRepository.existsByYearMonth(yearMonth)) {
            log.info("monthly statistics {} not found, calculating", yearMonth);
            Instant start = lastMonthStart.toInstant();
            Instant end = currentMonthStart.toInstant();
            StatsDto stats = eisMeasurementRepository.getStatsByCreationTimeBetween(start, end);
            log.info("year month: {}, total measurements: {}, total inspections: {}", yearMonth, stats.getTotalMeasurements(), stats.getTotalInspections());
            MonthlyStatsEntity statsEntity = MonthlyStatsEntity.builder()
                    .yearMonth(yearMonth)
                    .totalMeasurements(stats.getTotalMeasurements())
                    .totalInspections((int) stats.getTotalInspections().longValue())
                    .build();
            monthlyStatsRepository.upsert(statsEntity);
        }
    }

    public Overview getOverview() {
        StatsDto historicalStats = monthlyStatsRepository.getStats();
        ZonedDateTime startOfMonth = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
        StatsDto currentStats = eisMeasurementRepository.getStatsByCreationTimeBetween(startOfMonth.toInstant(), Instant.now());
        Overview overview = new Overview();
        overview.setTotalInspections(historicalStats.getTotalInspections() + currentStats.getTotalInspections());
        overview.setTotalMeasurements(historicalStats.getTotalMeasurements() + currentStats.getTotalMeasurements());
        overview.setMonthlyInspections((int) currentStats.getTotalInspections().longValue());
        return overview;
    }
}

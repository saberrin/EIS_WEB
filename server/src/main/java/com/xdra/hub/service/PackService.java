package com.xdra.hub.service;

import com.xdra.hub.analytics.AnalyticsService;
import com.xdra.hub.configuration.ConfigurationService;
import com.xdra.hub.entity.EisMeasurementEntity;
import com.xdra.hub.entity.GeneratedRecordEntity;
import com.xdra.hub.entity.PackMetricsRecordEntity;
import com.xdra.hub.model.*;
import com.xdra.hub.repository.EisMeasurementRepository;
import com.xdra.hub.repository.GeneratedRecordRepository;
import com.xdra.hub.repository.PackMetricsRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackService {

    private final EisMeasurementRepository eisMeasurementRepository;
    private final GeneratedRecordRepository generatedRecordRepository;
    private final PackMetricsRecordRepository packMetricsRecordRepository;
    private final ConfigurationService configurationService;

    @Value("${dashboard.number-pack-metrics:7}")
    private int numberOfPackMetrics;

    @Value("${dashboard.data-retention-days:30}")
    private int dataRetentionDays;

    public PackMetricsResponse getPackMetrics(String containerId, String clusterId, String packId) {
        Instant createdAfter = ZonedDateTime.now().minusDays(dataRetentionDays).toInstant();

        List<EisMeasurementEntity> eisMeasurements = eisMeasurementRepository.getPackLatestRecords(Long.parseLong(packId), createdAfter);
        PackMetricsResponse response = new PackMetricsResponse();
        response.setMeasurements(eisMeasurements.stream().map(entity -> {
            EisMeasurement measurement = new EisMeasurement();
            measurement.setContainerId(String.valueOf(entity.getContainerId()));
            measurement.setClusterId(String.valueOf(entity.getClusterId()));
            measurement.setPackId(String.valueOf(entity.getPackId()));
            measurement.setCellId(String.valueOf(entity.getCellId()));
            measurement.setRealImpedance(entity.getRealImpedance());
            measurement.setImaginaryImpedance(entity.getImaginaryImpedance());
            measurement.setFrequency(entity.getFrequency());
            measurement.setVoltage(entity.getVoltage());
            measurement.setTemperature(entity.getTemperature());
            measurement.setCreationTime(entity.getCreationTime().atOffset(ZoneOffset.systemDefault().getRules().getOffset(Instant.now())));
            return measurement;
        }).toList());

        PackMetrics metrics = new PackMetrics();
        metrics.setPackId(packId);
        response.setMetrics(metrics);

        List<PackMetricsRecordEntity> packMetricsRecords = packMetricsRecordRepository.getPackLatestNRecords(Long.parseLong(packId), createdAfter, numberOfPackMetrics);
        packMetricsRecords.stream().findFirst().ifPresentOrElse(packMetricsRecord -> {
            metrics.setDispersionCoefficient(packMetricsRecord.getDispersionCoefficient());
            metrics.setSafetyRate(packMetricsRecord.getSafetyRate());
        }, () -> log.info("No pack metrics record found for packId: {}", packId));
        metrics.setPackHealthHistory(packMetricsRecords.stream()
                .map(record -> {
                    PackHealth packHealth = new PackHealth();
                    packHealth.setSafetyRate(record.getSafetyRate());
                    packHealth.setCreationTime(record.getCreationTime().atOffset(ZoneOffset.systemDefault().getRules().getOffset(Instant.now())).toLocalDate());
                    return packHealth;
                })
                // make sure there are no duplicate records of the same day
                .collect(Collectors.toMap(PackHealth::getCreationTime, val -> val, (existing, replacement) -> existing))
                .values().stream().toList());

        List<GeneratedRecordEntity> generatedRecords = generatedRecordRepository.getPackLatestRecords(Long.parseLong(packId), createdAfter);
        metrics.setDiagnostics(buildDiagnostic(generatedRecords));

        return response;
    }

    private PackMetricsDiagnostics buildDiagnostic(List<GeneratedRecordEntity> generatedRecords) {
        PackMetricsDiagnostics diagnostics = new PackMetricsDiagnostics();
        Diagnostic max = new Diagnostic();
        diagnostics.setMax(max);
        Diagnostic min = new Diagnostic();
        diagnostics.setMin(min);
        for (GeneratedRecordEntity generatedRecord : generatedRecords) {
            if (max.getConductivity() == null || generatedRecord.getConductivity() > max.getConductivity()) {
                max.setConductivity(generatedRecord.getConductivity());
            }
            if (min.getConductivity() == null || generatedRecord.getConductivity() < min.getConductivity()) {
                min.setConductivity(generatedRecord.getConductivity());
            }
            if (max.getPolarizationPotential() == null || generatedRecord.getPolarizationPotential() > max.getPolarizationPotential()) {
                max.setPolarizationPotential(generatedRecord.getPolarizationPotential());
            }
            if (min.getPolarizationPotential() == null || generatedRecord.getPolarizationPotential() < min.getPolarizationPotential()) {
                min.setPolarizationPotential(generatedRecord.getPolarizationPotential());
            }
            if (max.getSeiParameter() == null || generatedRecord.getSeiParameter() > max.getSeiParameter()) {
                max.setSeiParameter(generatedRecord.getSeiParameter());
            }
            if (min.getSeiParameter() == null || generatedRecord.getSeiParameter() < min.getSeiParameter()) {
                min.setSeiParameter(generatedRecord.getSeiParameter());
            }
            if (max.getDendritesParameter() == null || generatedRecord.getDendritesParameter() > max.getDendritesParameter()) {
                max.setDendritesParameter(generatedRecord.getDendritesParameter());
            }
            if (min.getDendritesParameter() == null || generatedRecord.getDendritesParameter() < min.getDendritesParameter()) {
                min.setDendritesParameter(generatedRecord.getDendritesParameter());
            }
            if (max.getElectrolyteParameter() == null || generatedRecord.getElectrolyteParameter() > max.getElectrolyteParameter()) {
                max.setElectrolyteParameter(generatedRecord.getElectrolyteParameter());
            }
            if (min.getElectrolyteParameter() == null || generatedRecord.getElectrolyteParameter() < min.getElectrolyteParameter()) {
                min.setElectrolyteParameter(generatedRecord.getElectrolyteParameter());
            }
            if (max.getTemperature() == null || generatedRecord.getTemperature() > max.getTemperature()) {
                max.setTemperature(generatedRecord.getTemperature());
            }
            if (min.getTemperature() == null || generatedRecord.getTemperature() < min.getTemperature()) {
                min.setTemperature(generatedRecord.getTemperature());
            }
        }
        return diagnostics;
    }

    public ListPackOverviewsResponse listPackOverviews(String containerId, String clusterId) {
        List<Long> packIds = configurationService.getPackIds(containerId, clusterId).stream().map(Long::parseLong).toList();
        Instant createdAfter = ZonedDateTime.now().minusDays(dataRetentionDays).toInstant();
        List<EisMeasurementEntity> eisMeasurements = eisMeasurementRepository.getLatestRecordsGroupByPackId(packIds, createdAfter);
        List<PackMetricsRecordEntity> packMetricsRecords = packMetricsRecordRepository.getLatestRecordsGroupByPackId(packIds, createdAfter);

        Map<Long, PackOverview> overviewMap = getPackOverviewMap(packMetricsRecords);

        eisMeasurements.stream().collect(Collectors.groupingBy(EisMeasurementEntity::getPackId))
                .forEach((packId, eisMeasurementsForPack) -> {
                    PackOverview overview = overviewMap.get(packId);
                    if (overview == null) {
                        log.warn("No eis measurement records found for packId: {}", packId);
                        return;
                    }
                    List<AverageImpedanceRecord> averageImpedanceRecords = new LinkedList<>();
                    overview.setAverageImpedanceRecords(averageImpedanceRecords);
                    eisMeasurementsForPack.stream().collect(Collectors.groupingBy(EisMeasurementEntity::getFrequency))
                            .forEach((frequency, eisMeasurementsForFrequency) -> {
                                AverageImpedanceRecord averageImpedanceRecord = new AverageImpedanceRecord();
                                averageImpedanceRecord.setFrequency(frequency);
                                averageImpedanceRecord.setAverageImpedance(eisMeasurementsForFrequency.stream()
                                        .map(measurement -> Math.sqrt(Math.pow(measurement.getRealImpedance(), 2) + Math.pow(measurement.getImaginaryImpedance(), 2)))
                                        .reduce(0.0, Double::sum) / eisMeasurementsForFrequency.size());
                                averageImpedanceRecords.add(averageImpedanceRecord);
                            });
                });
        ListPackOverviewsResponse response = new ListPackOverviewsResponse();
        response.setRecords(new LinkedList<>(overviewMap.values()));
        return response;
    }

    private static Map<Long, PackOverview> getPackOverviewMap(List<PackMetricsRecordEntity> packMetricsRecords) {
        Map<Long, PackOverview> overviewMap = new HashMap<>();
        for (PackMetricsRecordEntity packMetricsRecord : packMetricsRecords) {
            overviewMap.computeIfAbsent(packMetricsRecord.getPackId(), k -> {
                PackOverview overview = new PackOverview();
                overview.setPackId(String.valueOf(packMetricsRecord.getPackId()));
                overview.setDispersionCoefficient(packMetricsRecord.getDispersionCoefficient());
                return overview;
            });
        }
        return overviewMap;
    }
}

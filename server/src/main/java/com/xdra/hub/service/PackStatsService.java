package com.xdra.hub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdra.hub.entity.PackStatisticsEntity;
import com.xdra.hub.model.*;
import com.xdra.hub.repository.PackStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackStatsService {

    private final PackStatisticsRepository packStatisticsRepository;
    private final ObjectMapper objectMapper;

    public PackMetricsResponse getPackMetrics(String packId) {
        List<PackStatisticsEntity> packStatistics = packStatisticsRepository.findTopNByPackIdOrderByCreationTimeDesc(Long.parseLong(packId), Limit.of(10));
        PackMetricsResponse response = new PackMetricsResponse();

        if (packStatistics == null || packStatistics.isEmpty()) {
            return response;
        }

        // Build packHistogram
        response.setPackHistogram(packStatistics.stream().map(entity -> {
            PackHistogramPoint point = new PackHistogramPoint();
            point.setAbsImpedanceMean(entity.getAbsImpedanceMean());
            point.setMaxAbsImpedance(entity.getMaxAbsImpedance());
            point.setMinAbsImpedance(entity.getMinAbsImpedance());
            point.setImpedanceStdDev(entity.getImpedanceStdDev());
            return point;
        }).toList());

        // Use the first entity for metrics, suggestion, characteristicFrequencies, cellColors
        PackStatisticsEntity first = packStatistics.get(0);

        // metrics
        PackMetrics metrics = new PackMetrics();
        metrics.setAbsImpedanceMean(first.getAbsImpedanceMean());
        metrics.setImpedanceStdDev(first.getImpedanceStdDev());
        metrics.setCoefficientOfVariation(first.getCoefficientOfVariation());
        metrics.setDispersion(first.getDispersion());
        metrics.setDegradationLevel(DegradationLevel.fromValue(first.getDegradationLevel()));
        response.setMetrics(metrics);

        // suggestion
        response.setSuggestion(first.getSuggestion());

        // characteristicFrequencies (parse from JSON)
        if (first.getCharacteristicFrequencies() != null) {
            try {
                CharacteristicFrequencies cf = objectMapper.treeToValue(first.getCharacteristicFrequencies(), CharacteristicFrequencies.class);
                response.setCharacteristicFrequencies(cf);
            } catch (Exception e) {
                log.warn("Failed to parse characteristicFrequencies", e);
            }
        }

        // cellColors (parse from JSON array)
        if (first.getCellColors() != null && first.getCellColors().isArray()) {
            try {
                List<CellColorInfo> cellColors = objectMapper.readerForListOf(CellColorInfo.class).readValue(first.getCellColors());
                response.setCellColors(cellColors);
            } catch (Exception e) {
                log.warn("Failed to parse cellColors", e);
            }
        }

        return response;
    }

    public ContainerPackMetricsResponse getContainerPackStatistics(String containerId) {
        List<PackStatisticsEntity> entities = packStatisticsRepository.get10LatestPackStatisticsGroupByPackIdByContainerId(Long.parseLong(containerId));
        Map<Long, List<PackStatisticsEntity>> groupedByPackId = entities.stream()
                .collect(Collectors.groupingBy(PackStatisticsEntity::getPackId));

        List<ClusterPackHistogram> histograms = groupedByPackId.entrySet().stream().map(entry ->
                new ClusterPackHistogram()
                        .packId(String.valueOf(entry.getKey()))
                        .packHistogram(
                                entry.getValue().stream().map(entity -> new PackHistogramPoint()
                                                .absImpedanceMean(entity.getAbsImpedanceMean())
                                                .maxAbsImpedance(entity.getMaxAbsImpedance())
                                                .minAbsImpedance(entity.getMinAbsImpedance())
                                                .impedanceStdDev(entity.getImpedanceStdDev()))
                                        .toList()
                        )).toList();

        ContainerPackMetricsResponse response = new ContainerPackMetricsResponse();
        response.setPackHistograms(histograms);
        return response;
    }
}

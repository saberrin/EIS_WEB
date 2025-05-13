package com.xdra.hub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdra.hub.model.*;
import com.xdra.hub.repository.CellStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class CellStatsService {

    private final CellStatisticsRepository cellStatisticsRepository;
    private final ObjectMapper objectMapper;

    public CellMetricsResponse getCellMetrics(String packId, String cellId, Instant dateFrom, Instant dateTo) {
        CellMetricsResponse response = new CellMetricsResponse();
        // Convert packId and cellId to Long
        Long packIdLong = Long.valueOf(packId);
        Long cellIdLong = Long.valueOf(cellId);
        // Query repository
        var entities = cellStatisticsRepository.findLatestByPackIdAndCellIdBetweenDates(packIdLong, cellIdLong, dateFrom, dateTo);

        // Histogram: map all entities
        var histogram = entities.stream().map(e -> {
            var point = new CellHistogramPoint();
            point.setRealPart10Hz(e.getRealPart10Hz());
            point.setImagPart10Hz(e.getImaginaryPart10Hz());
            try {
                EquivalentCircuitDiagram diagram = objectMapper.treeToValue(e.getEquivalentCircuitData(), EquivalentCircuitDiagram.class);
                point.setR0(diagram.getR0());
            } catch (JsonProcessingException ex) {
                log.warn("Failed to parse EquivalentCircuitDiagram {}", e.getEquivalentCircuitData(), ex);
            }
            if (e.getCreationTime() != null) {
                point.setTimestamp(OffsetDateTime.ofInstant(e.getCreationTime(), ZoneId.of("Asia/Shanghai")));
            }
            return point;
        }).toList();
        response.setHistogram(histogram);

        // Use only the first entity for the rest
        if (!entities.isEmpty()) {
            var e = entities.get(0);
            try {
                if (e.getNyquistPlot() != null) {
                    response.setNyquistPlot(objectMapper.treeToValue(e.getNyquistPlot(), NyquistPlot.class));
                }
                if (e.getBodePlot() != null) {
                    response.setBodePlot(objectMapper.readerForListOf(BodePlotPoint.class).readValue(e.getBodePlot()));
                }
                if (e.getDrtPlot() != null) {
                    response.setDrtPlot(objectMapper.readerForListOf(DrtPlotPoint.class).readValue(e.getDrtPlot()));
                }
                if (e.getRealPartCorrelation() != null) {
                    response.setRealPartCorrelation(objectMapper.readerForListOf(Double.class).readValue(e.getRealPartCorrelation()));
                }
                if (e.getImagPartCorrelation() != null) {
                    response.setImagPartCorrelation(objectMapper.readerForListOf(Double.class).readValue(e.getImagPartCorrelation()));
                }
                if (e.getEquivalentCircuitData() != null) {
                    response.setEquivalentCircuitDiagram(objectMapper.treeToValue(e.getEquivalentCircuitData(), EquivalentCircuitDiagram.class));
                }
            } catch (Exception ex) {
                log.warn("Failed to map JSON fields in CellMetricsResponse", ex);
            }
            // Metrics
            var metrics = new CellMetrics();
            metrics.setAbsImpedanceMean(e.getAbsImpedanceMean());
            metrics.setImpedanceStdDev(e.getImpedanceStdDev());
            metrics.setCoefficientOfVariation(e.getCoefficientOfVariation());
            metrics.setMaxImpedanceStdDev(e.getMaxImpedanceStdDev());
            metrics.setMinImpedanceStdDev(e.getMinImpedanceStdDev());
            metrics.setMaxAbsImpedance(e.getMaxAbsImpedance());
            metrics.setMinAbsImpedance(e.getMinAbsImpedance());
            metrics.setMaxCoefficientOfVariation(e.getMaxCoefficientOfVariation());
            metrics.setMinCoefficientOfVariation(e.getMinCoefficientOfVariation());
            metrics.setImpedanceMeanToMaxRatio(e.getImpMeanToMaxRatio());
            metrics.setImpedanceMeanToMinRatio(e.getImpMeanToMinRatio());
            metrics.setImpedanceMeanToAvgRatio(e.getImpMeanToAvgRatio());
            metrics.setImpedanceStdDevToMaxRatio(e.getImpStdDevToMaxRatio());
            metrics.setImpedanceStdDevToMinRatio(e.getImpStdDevToMinRatio());
            metrics.setImpedanceStdDevToAvgRatio(e.getImpStdDevToAvgRatio());
            response.setMetrics(metrics);
        }
        return response;
    }
}

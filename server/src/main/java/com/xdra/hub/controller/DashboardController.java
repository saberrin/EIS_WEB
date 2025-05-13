package com.xdra.hub.controller;

import com.xdra.hub.analytics.AnalyticsService;
import com.xdra.hub.api.DashboardApi;
import com.xdra.hub.model.CellMetricsResponse;
import com.xdra.hub.model.ContainerPackMetricsResponse;
import com.xdra.hub.model.Overview;
import com.xdra.hub.model.PackMetricsResponse;
import com.xdra.hub.service.CellStatsService;
import com.xdra.hub.service.DataExportService;
import com.xdra.hub.service.PackStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class DashboardController implements DashboardApi {

    private final AnalyticsService analyticsService;
    private final PackStatsService packStatsService;
    private final CellStatsService cellStatsService;
    private final DataExportService dataExportService;

    @Override
    public ResponseEntity<Overview> getDashboardOverview() {
        return ResponseEntity.ok(analyticsService.getOverview());
    }

    @Override
    public ResponseEntity<ContainerPackMetricsResponse> getContainerPackMetrics(String containerId) {
        return ResponseEntity.ok(packStatsService.getContainerPackStatistics(containerId));
    }

    @Override
    public ResponseEntity<PackMetricsResponse> getPackMetrics(String containerId, String packId) {
        return ResponseEntity.ok(packStatsService.getPackMetrics(packId));
    }

    @Override
    public ResponseEntity<CellMetricsResponse> getCellMetrics(String containerId, String packId, String cellId, OffsetDateTime dateFrom, OffsetDateTime dateTo) {
        Instant start = (dateFrom != null) ? dateFrom.toInstant() : Instant.now().minus(7, ChronoUnit.DAYS);
        Instant end = (dateTo != null) ? dateTo.toInstant() : Instant.now();

        return ResponseEntity.ok(cellStatsService.getCellMetrics(packId, cellId, start, end));
    }

    @Override
    public ResponseEntity<Resource> exportData(OffsetDateTime dateFrom, OffsetDateTime dateTo) {
        try {
            PipedOutputStream pos = new PipedOutputStream();
            PipedInputStream pis = new PipedInputStream(pos);

            new Thread(() -> {
                try {
                    Instant start = dateFrom != null ? dateFrom.toInstant() : Instant.now().minus(7, ChronoUnit.DAYS);
                    Instant end = dateTo != null ? dateTo.toInstant() : Instant.now();
                    dataExportService.exportData(start, end, pos);
                } catch (Exception e) {
                    // Optionally log error
                } finally {
                    try {
                        pos.close();
                    } catch (Exception ignore) {
                    }
                }
            }).start();

            InputStreamResource resource = new InputStreamResource(pis);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + UUID.randomUUID() + ".csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Failed to stream CSV", e);
        }
    }
}

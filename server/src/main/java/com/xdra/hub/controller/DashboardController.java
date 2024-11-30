package com.xdra.hub.controller;

import com.xdra.hub.api.DashboardApi;
import com.xdra.hub.model.ListPackOverviewsResponse;
import com.xdra.hub.model.Overview;
import com.xdra.hub.model.PackMetricsResponse;
import com.xdra.hub.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class DashboardController implements DashboardApi {

    private final DashboardService dashboardService;

    @Override
    public ResponseEntity<Overview> getDashboardOverview() {
        return ResponseEntity.ok(dashboardService.getDashboardOverview());
    }

    @Override
    public ResponseEntity<PackMetricsResponse> getPackMetrics(String containerId, String clusterId, String packId) {
        return ResponseEntity.ok(dashboardService.getPackMetrics(containerId, clusterId, packId));
    }

    @Override
    public ResponseEntity<ListPackOverviewsResponse> listPackOverviews(String containerId, String clusterId) {
        return ResponseEntity.ok(dashboardService.listPackOverviews(containerId, clusterId));
    }
}

package com.xdra.hub.dashboard;

import com.xdra.hub.analytics.AnalyticsService;
import com.xdra.hub.model.*;
import com.xdra.hub.service.PackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardFacade {

    private final PackService packService;
    private final AnalyticsService analyticsService;

    public Overview getDashboardOverview() {
        return analyticsService.getOverview();
    }

    public PackMetricsResponse getPackMetrics(String containerId, String clusterId, String packId) {
        return packService.getPackMetrics(containerId, clusterId, packId);
    }

    public ListPackOverviewsResponse listPackOverviews(String containerId, String clusterId) {
        return packService.listPackOverviews(containerId, clusterId);
    }
}

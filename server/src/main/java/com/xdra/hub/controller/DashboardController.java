package com.xdra.hub.controller;

import com.xdra.hub.api.DashboardApi;
import com.xdra.hub.model.Overview;
import com.xdra.hub.dashboard.DashboardFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class DashboardController implements DashboardApi {

    private final DashboardFacade dashboardFacade;

    @Override
    public ResponseEntity<Overview> getDashboardOverview() {
        return ResponseEntity.ok(dashboardFacade.getDashboardOverview());
    }
}

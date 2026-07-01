package org.opensource.safety.controller;

import org.opensource.safety.dto.DashboardResponse;
import org.opensource.safety.service.DashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{seniorId}")
    public DashboardResponse get(@PathVariable Long seniorId) {
        return dashboardService.getDashboard(seniorId);
    }
}

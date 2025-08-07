package com.app.focus.controller;

import com.app.focus.dto.summary.FullUsageStatsDTO;
import com.app.focus.service.UsageStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/usage-statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class UsageStatsController {

    private final UsageStatsService usageStatsService;

    @GetMapping
    public FullUsageStatsDTO getFullUsageStatistics() {
        log.info("GET /admin/usage-statistics called to retrieve full usage statistics.");
        return usageStatsService.getFullStats();
    }
}

package com.app.focus.controller;

import com.app.focus.dto.summary.DailySummaryResponseDTO;
import com.app.focus.interfaces.IDailySummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
@Slf4j
public class DailySummaryController {

    private final IDailySummaryService dailySummaryService;

    @GetMapping("/today")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DailySummaryResponseDTO getTodaySummary() {
        log.info("GET /api/summary/today called to retrieve today's daily summary.");
        return dailySummaryService.getTodaySummary();
    }
}


package com.app.focus.interfaces;

import com.app.focus.dto.summary.DailySummaryResponseDTO;

public interface IDailySummaryService {
    DailySummaryResponseDTO getTodaySummary();
}

package com.app.focus.dto.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FullUsageStatsDTO {
    private long totalUsersRegistered;
    private long totalUserLogins;
    private long totalPersonalTasksCreated;
    private long totalEventsScheduled;
    private long totalGoalsCreated;
    private long totalDiaryEntries;
    private long totalHabitsTracked;
    private long totalPhysicalActivities;
    private long totalWorkTasksCreated;
    private long totalNotificationsSent;
    private long totalActiveUsersLast7Days;
}

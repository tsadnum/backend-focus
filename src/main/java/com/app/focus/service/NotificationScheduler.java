package com.app.focus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationSchedulerService schedulerService;

    @Scheduled(cron = "0 0 17 * * *")
    public void sendDailyTaskReminders() {
        schedulerService.checkTasksDueSoon();
    }

    @Scheduled(cron = "0 0/5 * * * *")
    public void sendHabitReminders() {
        schedulerService.checkIncompleteHabits();
    }

    @Scheduled(cron = "0 5 8 * * *")
    public void sendGoalDeadlineReminders() {
        schedulerService.checkGoalsApproachingDeadline();
    }

    @Scheduled(cron = "0 0/30 * * * *")
    public void sendUpcomingEventReminders() {
        schedulerService.checkUpcomingEvents();
    }
}

package com.app.focus.service;

import com.app.focus.dto.summary.FullUsageStatsDTO;
import com.app.focus.entity.enums.TaskType;
import com.app.focus.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsageStatsService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final DiaryEntryRepository diaryRepository;
    private final HabitRepository habitRepository;
    private final NotificationRepository notificationRepository;

    public FullUsageStatsDTO getFullStats() {
        log.info("Fetching usage statistics...");

        long usersRegistered = userRepository.count();
        log.info("Total users registered: {}", usersRegistered);

        long userLogins = userRepository.countByLastLoginAtIsNotNull();
        log.info("Total user logins: {}", userLogins);

        long personalTasks = taskRepository.countByType(TaskType.PERSONAL);
        log.info("Total personal tasks created: {}", personalTasks);

        long workTasks = taskRepository.countByType(TaskType.WORK);
        log.info("Total work tasks created: {}", workTasks);

        long eventsScheduled = eventRepository.count();
        log.info("Total events scheduled: {}", eventsScheduled);

        long goalsCreated = goalRepository.count();
        log.info("Total goals created: {}", goalsCreated);

        long diaryEntries = diaryRepository.count();
        log.info("Total diary entries: {}", diaryEntries);

        long habitsTracked = habitRepository.count();
        log.info("Total habits tracked: {}", habitsTracked);

        long notificationsSent = notificationRepository.count();
        log.info("Total notifications sent: {}", notificationsSent);

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        long activeUsers = userRepository.countByLastLoginAtAfter(sevenDaysAgo);
        log.info("Total active users in last 7 days: {}", activeUsers);

        log.info("All usage statistics successfully retrieved.");

        return FullUsageStatsDTO.builder()
                .totalUsersRegistered(usersRegistered)
                .totalUserLogins(userLogins)
                .totalPersonalTasksCreated(personalTasks)
                .totalWorkTasksCreated(workTasks)
                .totalEventsScheduled(eventsScheduled)
                .totalGoalsCreated(goalsCreated)
                .totalDiaryEntries(diaryEntries)
                .totalHabitsTracked(habitsTracked)
                .totalNotificationsSent(notificationsSent)
                .totalActiveUsersLast7Days(activeUsers)
                .build();
    }
}

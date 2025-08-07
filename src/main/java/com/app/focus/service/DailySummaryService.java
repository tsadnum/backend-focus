package com.app.focus.service;

import com.app.focus.dto.summary.DailySummaryResponseDTO;
import com.app.focus.dto.event.EventResponseDTO;
import com.app.focus.dto.focusSession.FocusSessionResponseDTO;
import com.app.focus.dto.goal.GoalResponseDTO;
import com.app.focus.dto.habit.HabitResponseDTO;
import com.app.focus.dto.task.TaskResponseDTO;
import com.app.focus.entity.enums.TaskStatus;
import com.app.focus.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailySummaryService implements IDailySummaryService {

    private final ITaskService taskService;
    private final IHabitService habitService;
    private final IGoalService goalService;
    private final IFocusSessionService focusSessionService;
    private final IEventService eventService;

    @Override
    public DailySummaryResponseDTO getTodaySummary() {
        log.info("Generating daily summary for the authenticated user.");

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        List<TaskResponseDTO> todayTasks = taskService.getUserTasks().stream()
                .filter(task -> isTaskRelevantToday(task, today))
                .collect(Collectors.toList());
        log.debug("Found {} relevant tasks for today.", todayTasks.size());

        List<HabitResponseDTO> activeHabits = habitService.getUserHabits().stream()
                .filter(habit -> isHabitActiveToday(habit, today))
                .collect(Collectors.toList());
        log.debug("Found {} active habits for today.", activeHabits.size());

        List<GoalResponseDTO> activeGoals = goalService.getUserGoals().stream()
                .filter(goal -> today.isBefore(goal.getTargetDate()))
                .collect(Collectors.toList());
        log.debug("Found {} ongoing goals.", activeGoals.size());

        List<FocusSessionResponseDTO> todayFocusSessions = focusSessionService.getTodaySessions();
        log.debug("Found {} focus sessions for today.", todayFocusSessions.size());

        List<EventResponseDTO> upcomingEvents = eventService.getUserEvents().stream()
                .filter(event -> isEventWithinNext24Hours(event, now))
                .collect(Collectors.toList());
        log.debug("Found {} upcoming events.", upcomingEvents.size());

        log.info("Successfully assembled daily summary.");
        return DailySummaryResponseDTO.builder()
                .todayTasks(todayTasks)
                .activeHabits(activeHabits)
                .activeGoals(activeGoals)
                .todayFocusSessions(todayFocusSessions)
                .upcomingEvents(upcomingEvents)
                .build();
    }

    private boolean isTaskRelevantToday(TaskResponseDTO task, LocalDate today) {
        return (today.equals(task.getStartDate()) || today.equals(task.getDueDate())) &&
                (task.getStatus() == TaskStatus.PENDING || task.getStatus() == TaskStatus.IN_PROGRESS);
    }

    private boolean isHabitActiveToday(HabitResponseDTO habit, LocalDate today) {
        return habit.isActive() && habit.getActiveDays().contains(today.getDayOfWeek().name());
    }

    private boolean isEventWithinNext24Hours(EventResponseDTO event, LocalDateTime now) {
        return !event.getStartDateTime().isBefore(now) &&
                event.getStartDateTime().isBefore(now.plusDays(1));
    }

}

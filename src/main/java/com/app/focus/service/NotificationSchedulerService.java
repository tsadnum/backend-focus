package com.app.focus.service;

import com.app.focus.dto.event.EventResponseDTO;
import com.app.focus.dto.goal.GoalResponseDTO;
import com.app.focus.dto.habit.HabitResponseDTO;
import com.app.focus.dto.task.TaskResponseDTO;
import com.app.focus.entity.User;
import com.app.focus.entity.enums.TaskStatus;
import com.app.focus.entity.enums.NotificationType;
import com.app.focus.interfaces.*;
import com.app.focus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSchedulerService {

    private final ITaskService taskService;
    private final IHabitService habitService;
    private final IHabitLogService habitLogService;
    private final IGoalService goalService;
    private final IEventService eventService;
    private final INotificationService notificationService;
    private final UserRepository userRepository;

    public void checkTasksDueSoon() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<User> users = userRepository.findAll();
        log.info("Checking tasks due soon for {} users", users.size());

        for (User user : users) {
            List<TaskResponseDTO> tasks = taskService.getTasksByUserId(user.getId()).stream()
                    .filter(task -> task.getDueDate() != null &&
                            (task.getDueDate().isEqual(today) || task.getDueDate().isEqual(tomorrow)) &&
                            task.getStatus() != TaskStatus.COMPLETED)
                    .toList();

            log.info("User {} has {} tasks due soon", user.getEmail(), tasks.size());

            for (TaskResponseDTO task : tasks) {
                String title = "Task Due Soon";
                String message = "The task \"" + task.getTitle() + "\" is due on " + task.getDueDate() + ".";

                if (!notificationService.existsSimilarNotification(user.getId(), title, message, today)) {
                    notificationService.createNotification(
                            user.getId(),
                            title,
                            message,
                            NotificationType.TASK_DUE
                    );
                }
            }
        }
    }

    public void checkIncompleteHabits() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        List<User> users = userRepository.findAll();

        log.info("Checking incomplete habits for {} users", users.size());

        for (User user : users) {
            List<HabitResponseDTO> habits = habitService.getHabitsByUserId(user.getId()).stream()
                    .filter(habit -> habit.isActive()
                            && habit.getActiveDays().contains(today.getDayOfWeek().name())
                            && habit.getReminderTimes() != null
                            && habit.getReminderTimes().stream()
                            .anyMatch(reminder -> now.isAfter(reminder.plusMinutes(5)))
                    )
                    .toList();

            for (HabitResponseDTO habit : habits) {
                boolean alreadyLogged = habitLogService.isHabitCompletedToday(habit.getId(), today);

                if (!alreadyLogged) {
                    String title = "Pending Habit";
                    String message = "You haven't completed the habit \"" + habit.getName() + "\" today.";

                    if (!notificationService.existsSimilarNotification(user.getId(), title, message, today)) {
                        log.info("User {} has not completed habit '{}'", user.getEmail(), habit.getName());

                        notificationService.createNotification(
                                user.getId(),
                                title,
                                message,
                                NotificationType.HABIT_MISSED
                        );
                    }
                }
            }
        }
    }

    public void checkGoalsApproachingDeadline() {
        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(3);

        List<User> users = userRepository.findAll();
        log.info("Checking goals approaching deadline for {} users", users.size());

        for (User user : users) {
            List<GoalResponseDTO> goals = goalService.getGoalsByUserId(user.getId()).stream()
                    .filter(goal -> goal.getTargetDate() != null &&
                            !goal.getTargetDate().isBefore(today) &&
                            !goal.getTargetDate().isAfter(limitDate) &&
                            (goal.getProgress() == null || goal.getProgress() < 100))
                    .toList();

            for (GoalResponseDTO goal : goals) {
                String title = "Goal Approaching Deadline";
                String message = "Your goal \"" + goal.getTitle() + "\" is due on " + goal.getTargetDate() + ".";

                if (!notificationService.existsSimilarNotification(user.getId(), title, message, today)) {
                    log.info("User {} has a goal approaching deadline: '{}'", user.getEmail(), goal.getTitle());

                    notificationService.createNotification(
                            user.getId(),
                            title,
                            message,
                            NotificationType.GOAL_NEAR_DEADLINE
                    );
                }
            }
        }
    }

    public void checkUpcomingEvents() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalTime inOneHour = now.plusHours(1);

        List<User> users = userRepository.findAll();
        log.info("Checking upcoming events for {} users", users.size());

        for (User user : users) {
            List<EventResponseDTO> upcomingEvents = eventService.getEventsByUserId(user.getId()).stream()
                    .filter(event -> event.getStartDateTime() != null &&
                            event.getStartDateTime().toLocalDate().isEqual(today) &&
                            event.getStartDateTime().toLocalTime().isAfter(now) &&
                            event.getStartDateTime().toLocalTime().isBefore(inOneHour))
                    .toList();

            for (EventResponseDTO event : upcomingEvents) {
                String title = "Upcoming Event";
                String message = "You have an upcoming event: \"" + event.getTitle() +
                        "\" at " + event.getStartDateTime().toLocalTime() + ".";

                createNotificationIfNotExists(user.getId(), title, message, NotificationType.EVENT_UPCOMING, today);
            }
        }
    }

    private void createNotificationIfNotExists(Long userId, String title, String message, NotificationType type, LocalDate date) {
        if (!notificationService.existsSimilarNotification(userId, title, message, date)) {
            log.info("Creating notification: {} for user ID {}", title, userId);
            notificationService.createNotification(userId, title, message, type);
        }
    }

}

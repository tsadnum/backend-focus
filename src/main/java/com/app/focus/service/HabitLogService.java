package com.app.focus.service;

import com.app.focus.dto.habit.HabitLogRequestDTO;
import com.app.focus.dto.habit.HabitLogResponseDTO;
import com.app.focus.entity.Habit;
import com.app.focus.entity.HabitLog;
import com.app.focus.entity.User;
import com.app.focus.interfaces.IHabitLogService;
import com.app.focus.repository.HabitLogRepository;
import com.app.focus.repository.HabitRepository;
import com.app.focus.security.AuthenticatedUserProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HabitLogService implements IHabitLogService {

    private final HabitLogRepository habitLogRepository;
    private final HabitRepository habitRepository;
    private final AuthenticatedUserProvider authUserProvider;
    private final ModelMapper modelMapper;

    @Override
    public HabitLogResponseDTO saveLog(HabitLogRequestDTO dto) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Creating habit log for user: {} and habit ID: {}", user.getEmail(), dto.getHabitId());

        Habit habit = getUserHabitOrThrow(dto.getHabitId(), user);

        HabitLog habitLog = new HabitLog();
        habitLog.setHabit(habit);
        habitLog.setCompleted(dto.isCompleted());
        habitLog.setCompletionTime(dto.getCompletionTime());

        HabitLog saved = habitLogRepository.save(habitLog);
        log.info("Habit log saved for habit ID: {} at {}", habit.getId(), dto.getCompletionTime());

        HabitLogResponseDTO response = modelMapper.map(saved, HabitLogResponseDTO.class);
        response.setHabitId(saved.getHabit().getId());

        return response;
    }

    @Override
    public List<HabitLogResponseDTO> getLogsForUser() {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Fetching all habit logs for user: {}", user.getEmail());

        return habitLogRepository.findByHabit_User(user).stream()
                .map(log -> {
                    HabitLogResponseDTO dto = modelMapper.map(log, HabitLogResponseDTO.class);
                    dto.setHabitId(log.getHabit().getId());
                    return dto;
                })
                .toList();
    }


    @Override
    public List<HabitLogResponseDTO> getLogsByDate(LocalDate date) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Fetching habit logs for user: {} on {}", user.getEmail(), date);

        return habitLogRepository.findByUserAndDate(user, date).stream()
                .map(log -> {
                    HabitLogResponseDTO dto = modelMapper.map(log, HabitLogResponseDTO.class);
                    dto.setHabitId(log.getHabit().getId());
                    return dto;
                })
                .toList();
    }

    @Override
    public boolean isHabitCompletedToday(Long habitId, LocalDate date) {
        log.info("Checking if habit ID: {} is completed on {}", habitId, date);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        boolean completed = habitLogRepository.existsCompletedLogToday(habitId, startOfDay, endOfDay);
        log.debug("Habit ID: {} completed today: {}", habitId, completed);
        return completed;
    }

    private Habit getUserHabitOrThrow(Long habitId, User user) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> {
                    log.warn("Habit not found with ID: {}", habitId);
                    return new EntityNotFoundException("Habit not found");
                });

        if (!habit.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized habit access attempt by user: {} for habit ID: {}", user.getEmail(), habitId);
            throw new AccessDeniedException("Not authorized to access this habit");
        }

        return habit;
    }
}

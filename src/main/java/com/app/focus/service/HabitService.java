package com.app.focus.service;

import com.app.focus.dto.habit.HabitRequestDTO;
import com.app.focus.dto.habit.HabitResponseDTO;
import com.app.focus.entity.Habit;
import com.app.focus.entity.User;
import com.app.focus.interfaces.IHabitService;
import com.app.focus.repository.HabitRepository;
import com.app.focus.security.AuthenticatedUserProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HabitService implements IHabitService {

    private final HabitRepository habitRepository;
    private final AuthenticatedUserProvider authUserProvider;
    private final ModelMapper modelMapper;

    @Override
    public HabitResponseDTO createHabit(HabitRequestDTO dto) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Creating habit for user: {}", user.getEmail());

        Habit habit = modelMapper.map(dto, Habit.class);
        habit.setUser(user);
        Habit saved = habitRepository.save(habit);

        log.info("Habit created with ID: {}", saved.getId());
        return modelMapper.map(saved, HabitResponseDTO.class);
    }

    @Override
    public List<HabitResponseDTO> getUserHabits() {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Listing habits for user: {}", user.getEmail());

        List<Habit> habits = habitRepository.findByUser(user);
        log.debug("Total habits found for user {}: {}", user.getEmail(), habits.size());

        return habits.stream()
                .map(habit -> modelMapper.map(habit, HabitResponseDTO.class))
                .toList();
    }

    @Override
    public HabitResponseDTO updateHabit(Long id, HabitRequestDTO dto) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Updating habit ID: {} for user: {}", id, user.getEmail());

        Habit habit = getHabitIfOwnerOrThrow(id, user);
        modelMapper.map(dto, habit);
        Habit updated = habitRepository.save(habit);

        log.info("Habit with ID: {} updated successfully", updated.getId());
        return modelMapper.map(updated, HabitResponseDTO.class);
    }

    @Override
    public void deleteHabit(Long id) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Deleting habit ID: {} for user: {}", id, user.getEmail());

        Habit habit = getHabitIfOwnerOrThrow(id, user);
        habitRepository.delete(habit);

        log.info("Habit with ID: {} deleted successfully", id);
    }

    @Override
    @Transactional
    public List<HabitResponseDTO> getHabitsByUserId(Long userId) {
        log.info("Fetching habits for user ID: {}", userId);

        List<Habit> habits = habitRepository.findByUserId(userId);

        habits.forEach(habit -> {
            habit.getActiveDays().size();
            habit.getReminderTimes().size();
        });

        List<HabitResponseDTO> responseList = habits.stream()
                .map(habit -> modelMapper.map(habit, HabitResponseDTO.class))
                .toList();

        log.debug("Habits retrieved for user ID {}: {}", userId, responseList.size());
        return responseList;
    }

    private Habit getHabitIfOwnerOrThrow(Long habitId, User user) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> {
                    log.warn("Habit not found with ID: {}", habitId);
                    return new EntityNotFoundException("Habit not found");
                });

        if (!habit.getUser().getId().equals(user.getId())) {
            log.warn("Access denied - User {} attempted to access habit ID: {}", user.getEmail(), habitId);
            throw new AccessDeniedException("Unauthorized to access this habit");
        }

        return habit;
    }
}

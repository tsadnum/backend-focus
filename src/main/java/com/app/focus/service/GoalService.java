package com.app.focus.service;

import com.app.focus.dto.goal.GoalRequestDTO;
import com.app.focus.dto.goal.GoalResponseDTO;
import com.app.focus.entity.Goal;
import com.app.focus.entity.User;
import com.app.focus.interfaces.IGoalService;
import com.app.focus.repository.GoalRepository;
import com.app.focus.security.AuthenticatedUserProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService implements IGoalService {

    private final GoalRepository goalRepository;
    private final ModelMapper modelMapper;
    private final AuthenticatedUserProvider authUserProvider;

    @Override
    public GoalResponseDTO createGoal(GoalRequestDTO dto) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Creating new goal for user: {}", user.getEmail());

        if (dto.getTargetDate().isBefore(LocalDate.now())) {
            log.warn("Invalid goal target date: {}", dto.getTargetDate());
            throw new IllegalArgumentException("La fecha objetivo debe ser igual o posterior a la fecha actual.");
        }

        Goal goal = modelMapper.map(dto, Goal.class);
        goal.setUser(user);
        Goal saved = goalRepository.save(goal);

        log.info("Goal created successfully with ID: {}", saved.getId());
        return modelMapper.map(saved, GoalResponseDTO.class);
    }


    @Override
    public List<GoalResponseDTO> getUserGoals() {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Fetching goals for user: {}", user.getEmail());

        List<Goal> goals = goalRepository.findByUser(user);
        log.debug("Number of goals retrieved for user {}: {}", user.getEmail(), goals.size());

        return goals.stream()
                .map(goal -> modelMapper.map(goal, GoalResponseDTO.class))
                .toList();
    }

    @Override
    public GoalResponseDTO updateGoal(Long id, GoalRequestDTO dto) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Updating goal ID: {} for user: {}", id, user.getEmail());

        if (dto.getTargetDate().isBefore(LocalDate.now())) {
            log.warn("Invalid goal update: target date {} is before today", dto.getTargetDate());
            throw new IllegalArgumentException("La fecha objetivo debe ser igual o posterior a la fecha actual.");
        }

        Goal existing = getUserGoalOrThrow(id, user.getId());
        modelMapper.map(dto, existing);
        Goal updated = goalRepository.save(existing);

        log.info("Goal with ID: {} updated successfully", updated.getId());
        return modelMapper.map(updated, GoalResponseDTO.class);
    }

    @Override
    public void deleteGoal(Long id) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Deleting goal ID: {} for user: {}", id, user.getEmail());

        Goal goal = getUserGoalOrThrow(id, user.getId());

        goalRepository.delete(goal);
        log.info("Goal with ID: {} deleted successfully", id);
    }

    @Override
    public List<GoalResponseDTO> getGoalsByUserId(Long userId) {
        User currentUser = authUserProvider.getAuthenticatedUser();

        if (!currentUser.getId().equals(userId)) {
            log.warn("Unauthorized access: user {} tried to access goals of user ID: {}", currentUser.getEmail(), userId);
            throw new AccessDeniedException("Not authorized to access goals for this user");
        }

        log.info("Fetching goals for user ID: {}", userId);

        List<Goal> goals = goalRepository.findByUserId(userId);
        log.debug("Number of goals found for user ID {}: {}", userId, goals.size());

        return goals.stream()
                .map(goal -> modelMapper.map(goal, GoalResponseDTO.class))
                .toList();
    }

    private Goal getUserGoalOrThrow(Long id, Long userId) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Goal not found with ID: {}", id);
                    return new EntityNotFoundException("Goal not found");
                });

        if (!goal.getUser().getId().equals(userId)) {
            log.warn("Unauthorized access: user ID {} tried to access goal ID: {}", userId, id);
            throw new AccessDeniedException("Not authorized to access this goal");
        }

        return goal;
    }
}

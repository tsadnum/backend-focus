package com.app.focus.interfaces;

import com.app.focus.dto.goal.GoalRequestDTO;
import com.app.focus.dto.goal.GoalResponseDTO;
import java.util.List;

public interface IGoalService {
    GoalResponseDTO createGoal(GoalRequestDTO dto);
    List<GoalResponseDTO> getUserGoals();
    GoalResponseDTO updateGoal(Long id, GoalRequestDTO dto);
    void deleteGoal(Long id);
    List<GoalResponseDTO> getGoalsByUserId(Long userId);

}

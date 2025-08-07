package com.app.focus.interfaces;

import com.app.focus.dto.habit.HabitRequestDTO;
import com.app.focus.dto.habit.HabitResponseDTO;
import java.util.List;

public interface IHabitService {
    HabitResponseDTO createHabit(HabitRequestDTO dto);
    List<HabitResponseDTO> getUserHabits();
    HabitResponseDTO updateHabit(Long id, HabitRequestDTO dto);
    void deleteHabit(Long id);
    List<HabitResponseDTO> getHabitsByUserId(Long userId);


}

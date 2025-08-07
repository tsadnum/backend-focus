package com.app.focus.interfaces;

import com.app.focus.dto.habit.HabitLogRequestDTO;
import com.app.focus.dto.habit.HabitLogResponseDTO;
import java.time.LocalDate;
import java.util.List;

public interface IHabitLogService {
    HabitLogResponseDTO saveLog(HabitLogRequestDTO dto);
    List<HabitLogResponseDTO> getLogsForUser();
    List<HabitLogResponseDTO> getLogsByDate(LocalDate date);
    boolean isHabitCompletedToday(Long habitId, LocalDate date);
}

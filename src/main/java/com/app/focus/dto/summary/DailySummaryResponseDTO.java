package com.app.focus.dto.summary;

import com.app.focus.dto.task.TaskResponseDTO;
import com.app.focus.dto.habit.HabitResponseDTO;
import com.app.focus.dto.goal.GoalResponseDTO;
import com.app.focus.dto.focusSession.FocusSessionResponseDTO;
import com.app.focus.dto.event.EventResponseDTO;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DailySummaryResponseDTO {

    private List<TaskResponseDTO> todayTasks;

    private List<HabitResponseDTO> activeHabits;

    private List<GoalResponseDTO> activeGoals;

    private List<FocusSessionResponseDTO> todayFocusSessions;

    private List<EventResponseDTO> upcomingEvents;
}

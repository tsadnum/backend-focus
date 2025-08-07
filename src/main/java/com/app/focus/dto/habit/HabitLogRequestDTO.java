package com.app.focus.dto.habit;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HabitLogRequestDTO {
    private Long habitId;
    private LocalDateTime completionTime;
    private boolean isCompleted;
}

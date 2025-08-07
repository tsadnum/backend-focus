package com.app.focus.dto.task;

import com.app.focus.entity.enums.TaskPriority;
import com.app.focus.entity.enums.TaskStatus;
import com.app.focus.entity.enums.TaskType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SubtaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private TaskType type;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Long parentTaskId;
}

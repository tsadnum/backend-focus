package com.app.focus.dto.task;

import com.app.focus.entity.enums.TaskPriority;
import com.app.focus.entity.enums.TaskStatus;
import com.app.focus.entity.enums.TaskType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskResponseDTO {

    private Long id;

    private String title;

    private String description;

    private TaskType type;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDate startDate;

    private LocalDate dueDate;

    private Integer kanbanOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

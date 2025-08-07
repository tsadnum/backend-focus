package com.app.focus.dto.task;

import com.app.focus.entity.enums.TaskPriority;
import com.app.focus.entity.enums.TaskStatus;
import com.app.focus.entity.enums.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequestDTO {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private TaskType type;

    @NotNull
    private TaskStatus status;

    @NotNull
    private TaskPriority priority;

    private LocalDate startDate;

    private LocalDate dueDate;

    private Integer kanbanOrder;
}

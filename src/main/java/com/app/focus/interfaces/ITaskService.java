package com.app.focus.interfaces;

import com.app.focus.dto.task.TaskRequestDTO;
import com.app.focus.dto.task.TaskResponseDTO;
import com.app.focus.entity.enums.TaskStatus;
import com.app.focus.entity.enums.TaskType;

import java.util.List;

public interface ITaskService {
    TaskResponseDTO createTask(TaskRequestDTO dto);
    List<TaskResponseDTO> getUserTasks();
    TaskResponseDTO updateTask(Long id, TaskRequestDTO dto);
    void deleteTask(Long id);
    List<TaskResponseDTO> getUserTasksByType(TaskType type);
    List<TaskResponseDTO> getTasksByStatusOrdered(TaskStatus status);
    List<TaskResponseDTO> getTasksByUserId(Long userId);


}

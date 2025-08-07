package com.app.focus.service;

import com.app.focus.dto.task.TaskRequestDTO;
import com.app.focus.dto.task.TaskResponseDTO;
import com.app.focus.entity.Task;
import com.app.focus.entity.User;
import com.app.focus.entity.enums.TaskStatus;
import com.app.focus.entity.enums.TaskType;
import com.app.focus.interfaces.ITaskService;
import com.app.focus.repository.TaskRepository;
import com.app.focus.repository.UserRepository;
import com.app.focus.security.AuthenticatedUserProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService implements ITaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserProvider authUserProvider;
    private final ModelMapper modelMapper;

    @Override
    public TaskResponseDTO createTask(TaskRequestDTO dto) {
        User user = authUserProvider.getAuthenticatedUser();

        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        log.info("Creating task for user: {}", managedUser.getEmail());

        Task task = modelMapper.map(dto, Task.class);
        task.setUser(managedUser);
        Task saved = taskRepository.save(task);

        log.info("Task successfully created with ID: {}", saved.getId());
        return modelMapper.map(saved, TaskResponseDTO.class);
    }

    @Override
    public List<TaskResponseDTO> getUserTasks() {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Fetching tasks for user: {}", user.getEmail());

        List<Task> tasks = taskRepository.findByUser(user);
        log.debug("Total tasks found: {}", tasks.size());

        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO dto) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Updating task ID: {} for user: {}", id, user.getEmail());

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with ID: {}", id);
                    return new EntityNotFoundException("Task not found");
                });

        if (!Objects.equals(task.getUser().getId(), user.getId())) {
            log.warn("Access denied: user {} tried to update task ID: {}", user.getEmail(), id);
            throw new AccessDeniedException("Unauthorized to modify this task");
        }

        modelMapper.map(dto, task);
        Task updated = taskRepository.save(task);

        log.info("Task successfully updated with ID: {}", updated.getId());
        return modelMapper.map(updated, TaskResponseDTO.class);
    }

    @Override
    public void deleteTask(Long id) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Deleting task ID: {} for user: {}", id, user.getEmail());

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with ID: {}", id);
                    return new EntityNotFoundException("Task not found");
                });

        if (!Objects.equals(task.getUser().getId(), user.getId())) {
            log.warn("Access denied: user {} tried to delete task ID: {}", user.getEmail(), id);
            throw new AccessDeniedException("Unauthorized to delete this task");
        }

        taskRepository.delete(task);
        log.info("Task successfully deleted with ID: {}", id);
    }

    @Override
    public List<TaskResponseDTO> getUserTasksByType(TaskType type) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Fetching tasks of type {} for user: {}", type, user.getEmail());

        List<Task> tasks = taskRepository.findByUserAndType(user, type);
        log.debug("Found {} tasks of type {}", tasks.size(), type);

        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponseDTO> getTasksByStatusOrdered(TaskStatus status) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Fetching tasks with status {} for user ID: {}", status, user.getId());

        List<Task> tasks = taskRepository.findByUserIdAndStatusOrderByKanbanOrderAsc(user.getId(), status);
        log.debug("Found {} tasks with status {}", tasks.size(), status);

        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponseDTO> getTasksByUserId(Long userId) {
        log.info("Fetching tasks for user ID: {}", userId);
        List<Task> tasks = taskRepository.findByUserIdOrderByDueDateAsc(userId);
        log.debug("Found {} tasks for user ID: {}", tasks.size(), userId);

        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskResponseDTO.class))
                .collect(Collectors.toList());
    }
}

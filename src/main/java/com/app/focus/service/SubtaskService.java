package com.app.focus.service;

import com.app.focus.dto.task.SubtaskRequestDTO;
import com.app.focus.dto.task.SubtaskResponseDTO;
import com.app.focus.entity.Task;
import com.app.focus.entity.User;
import com.app.focus.interfaces.ISubtaskService;
import com.app.focus.repository.TaskRepository;
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
public class SubtaskService implements ISubtaskService {

    private final TaskRepository taskRepository;
    private final AuthenticatedUserProvider authUserProvider;
    private final ModelMapper modelMapper;

    @Override
    public SubtaskResponseDTO createSubtask(Long parentTaskId, SubtaskRequestDTO dto) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Creating subtask for parent task ID: {} by user: {}", parentTaskId, user.getEmail());

        Task parent = getTaskOwnedByUserOrThrow(parentTaskId, false);

        Task subtask = modelMapper.map(dto, Task.class);
        subtask.setUser(user);
        subtask.setParentTask(parent);
        Task saved = taskRepository.save(subtask);

        SubtaskResponseDTO response = modelMapper.map(saved, SubtaskResponseDTO.class);
        response.setParentTaskId(parent.getId());

        log.info("Subtask successfully created with ID: {}", saved.getId());
        return response;
    }

    @Override
    public List<SubtaskResponseDTO> getSubtasksByParentId(Long parentTaskId) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Fetching subtasks for parent task ID: {} by user: {}", parentTaskId, user.getEmail());

        getTaskOwnedByUserOrThrow(parentTaskId, false);

        List<Task> subtasks = taskRepository.findByParentTaskId(parentTaskId);
        log.debug("Subtasks retrieved: {}", subtasks.size());

        return subtasks.stream().map(subtask -> {
            SubtaskResponseDTO dto = modelMapper.map(subtask, SubtaskResponseDTO.class);
            dto.setParentTaskId(parentTaskId);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public SubtaskResponseDTO updateSubtask(Long subtaskId, SubtaskRequestDTO dto) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Updating subtask ID: {} by user: {}", subtaskId, user.getEmail());

        Task subtask = getTaskOwnedByUserOrThrow(subtaskId, true);

        Task originalParent = subtask.getParentTask();

        modelMapper.map(dto, subtask);
        subtask.setParentTask(originalParent);

        Task updated = taskRepository.save(subtask);

        SubtaskResponseDTO response = modelMapper.map(updated, SubtaskResponseDTO.class);
        response.setParentTaskId(originalParent.getId());

        log.info("Subtask updated successfully. ID: {}", subtaskId);
        return response;
    }

    @Override
    public void deleteSubtask(Long subtaskId) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Deleting subtask ID: {} by user: {}", subtaskId, user.getEmail());

        Task subtask = getTaskOwnedByUserOrThrow(subtaskId, true);
        taskRepository.delete(subtask);

        log.info("Subtask deleted successfully. ID: {}", subtaskId);
    }


    private Task getTaskOwnedByUserOrThrow(Long taskId, boolean mustBeSubtask) {
        User user = authUserProvider.getAuthenticatedUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.warn("Task not found. ID: {}", taskId);
                    return new EntityNotFoundException("Task not found");
                });

        if (!Objects.equals(task.getUser().getId(), user.getId())) {
            log.warn("Access denied. User {} does not own task ID: {}", user.getEmail(), taskId);
            throw new AccessDeniedException("Not authorized to access this task");
        }

        if (mustBeSubtask && task.getParentTask() == null) {
            log.warn("Task ID: {} is not a subtask", taskId);
            throw new AccessDeniedException("This task is not a subtask");
        }

        if (!mustBeSubtask && task.getParentTask() != null) {
            log.warn("Task ID: {} is a subtask, not a parent task", taskId);
            throw new AccessDeniedException("This is not a parent task");
        }

        return task;
    }
}

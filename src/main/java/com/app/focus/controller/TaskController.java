package com.app.focus.controller;

import com.app.focus.dto.task.TaskRequestDTO;
import com.app.focus.dto.task.TaskResponseDTO;
import com.app.focus.entity.enums.TaskStatus;
import com.app.focus.entity.enums.TaskType;
import com.app.focus.interfaces.ITaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final ITaskService taskService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody @Valid TaskRequestDTO dto) {
        log.info("POST /api/tasks - Creating a new task");
        log.debug("Task payload: {}", dto);

        TaskResponseDTO response = taskService.createTask(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        log.info("Task created successfully with ID {}", response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<TaskResponseDTO>> getUserTasks() {
        log.info("GET /api/tasks - Fetching all tasks for the authenticated user");
        List<TaskResponseDTO> tasks = taskService.getUserTasks();
        log.debug("Total tasks retrieved: {}", tasks.size());
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable @Positive Long id,
                                                      @RequestBody @Valid TaskRequestDTO dto) {
        log.info("PUT /api/tasks/{} - Updating task", id);
        log.debug("Update payload: {}", dto);

        TaskResponseDTO updated = taskService.updateTask(id, dto);

        log.info("Task ID {} updated successfully", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable @Positive Long id) {
        log.info("DELETE /api/tasks/{} - Deleting task", id);
        taskService.deleteTask(id);
        log.info("Task ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByType(@PathVariable TaskType type) {
        log.info("GET /api/tasks/type/{} - Fetching tasks by type", type);
        List<TaskResponseDTO> tasks = taskService.getUserTasksByType(type);
        log.debug("Tasks found for type {}: {}", type, tasks.size());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/kanban")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<TaskStatus, List<TaskResponseDTO>>> getKanbanView() {
        log.info("GET /api/tasks/kanban - Generating Kanban view");

        Map<TaskStatus, List<TaskResponseDTO>> kanban = new EnumMap<>(TaskStatus.class);
        for (TaskStatus status : TaskStatus.values()) {
            List<TaskResponseDTO> tasks = taskService.getTasksByStatusOrdered(status);
            log.debug("Tasks in status {}: {}", status, tasks.size());
            kanban.put(status, tasks);
        }

        log.info("Kanban view generated successfully");
        return ResponseEntity.ok(kanban);
    }
}

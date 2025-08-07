package com.app.focus.controller;

import com.app.focus.dto.task.SubtaskRequestDTO;
import com.app.focus.dto.task.SubtaskResponseDTO;
import com.app.focus.interfaces.ISubtaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks/{parentId}/subtasks")
@RequiredArgsConstructor
@Slf4j
public class SubtaskController {

    private final ISubtaskService subtaskService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<SubtaskResponseDTO>> getSubtasks(@PathVariable @Positive Long parentId) {
        log.info("GET /api/tasks/{}/subtasks - Fetching subtasks for parent task", parentId);
        List<SubtaskResponseDTO> subtasks = subtaskService.getSubtasksByParentId(parentId);
        log.debug("Retrieved {} subtasks for parent task {}", subtasks.size(), parentId);
        return ResponseEntity.ok(subtasks);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SubtaskResponseDTO> createSubtask(
            @PathVariable @Positive Long parentId,
            @RequestBody @Valid SubtaskRequestDTO dto) {

        log.info("POST /api/tasks/{}/subtasks - Creating subtask", parentId);
        log.debug("Subtask creation request: {}", dto);

        SubtaskResponseDTO created = subtaskService.createSubtask(parentId, dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        log.info("Subtask created with ID {} under parent task {}", created.getId(), parentId);
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{subtaskId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SubtaskResponseDTO> updateSubtask(
            @PathVariable @Positive Long parentId,
            @PathVariable @Positive Long subtaskId,
            @RequestBody @Valid SubtaskRequestDTO dto) {

        log.info("PUT /api/tasks/{}/subtasks/{} - Updating subtask", parentId, subtaskId);
        log.debug("Subtask update request: {}", dto);

        SubtaskResponseDTO updated = subtaskService.updateSubtask(subtaskId, dto);

        log.info("Subtask {} updated successfully", subtaskId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{subtaskId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSubtask(
            @PathVariable @Positive Long parentId,
            @PathVariable @Positive Long subtaskId) {

        log.info("DELETE /api/tasks/{}/subtasks/{} - Deleting subtask", parentId, subtaskId);

        subtaskService.deleteSubtask(subtaskId);

        log.info("Subtask {} deleted successfully", subtaskId);
        return ResponseEntity.noContent().build();
    }
}

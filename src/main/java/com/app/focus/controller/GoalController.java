package com.app.focus.controller;

import com.app.focus.dto.goal.GoalRequestDTO;
import com.app.focus.dto.goal.GoalResponseDTO;
import com.app.focus.interfaces.IGoalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Slf4j
public class GoalController {

    private final IGoalService goalService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<GoalResponseDTO> createGoal(@RequestBody @Valid GoalRequestDTO dto) {
        log.info("POST /api/goals - Creating new goal");
        log.debug("Received payload: {}", dto);
        GoalResponseDTO response = goalService.createGoal(dto);
        log.info("Goal created with ID: {}", response.getId());
        return ResponseEntity.created(URI.create("/goals/" + response.getId())).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<GoalResponseDTO>> getUserGoals() {
        log.info("GET /api/goals - Listing user's goals");
        List<GoalResponseDTO> goals = goalService.getUserGoals();
        log.debug("Number of goals found: {}", goals.size());
        return ResponseEntity.ok(goals);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<GoalResponseDTO> updateGoal(@PathVariable @Positive Long id,
                                                      @RequestBody @Valid GoalRequestDTO dto) {
        log.info("PUT /api/goals/{} - Updating goal", id);
        log.debug("Update payload: {}", dto);
        GoalResponseDTO updated = goalService.updateGoal(id, dto);
        log.info("Goal successfully updated");
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGoal(@PathVariable @Positive Long id) {
        log.info("DELETE /api/goals/{} - Deleting goal", id);
        goalService.deleteGoal(id);
        log.info("Goal successfully deleted");
        return ResponseEntity.noContent().build();
    }
}

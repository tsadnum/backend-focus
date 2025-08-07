package com.app.focus.controller;

import com.app.focus.dto.habit.HabitRequestDTO;
import com.app.focus.dto.habit.HabitResponseDTO;
import com.app.focus.interfaces.IHabitService;
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
@RequestMapping("/api/habits")
@RequiredArgsConstructor
@Slf4j
public class HabitController {

    private final IHabitService habitService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<HabitResponseDTO> createHabit(@RequestBody @Valid HabitRequestDTO dto) {
        log.info("POST /api/habits - Creating habit: {}", dto.getName());
        HabitResponseDTO created = habitService.createHabit(dto);
        log.info("Habit created with ID: {}", created.getId());
        URI location = URI.create("/api/habits/" + created.getId());
        return ResponseEntity.created(location).body(created); // 201 Created + Location
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<HabitResponseDTO>> getUserHabits() {
        log.info("GET /api/habits - Fetching habits for authenticated user");
        List<HabitResponseDTO> habits = habitService.getUserHabits();
        log.debug("Number of habits retrieved: {}", habits.size());
        return ResponseEntity.ok(habits);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<HabitResponseDTO> updateHabit(@PathVariable @Positive Long id,
                                                        @RequestBody @Valid HabitRequestDTO dto) {
        log.info("PUT /api/habits/{} - Updating habit to: {}", id, dto.getName());
        HabitResponseDTO updated = habitService.updateHabit(id, dto);
        log.info("Habit with ID {} updated successfully", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHabit(@PathVariable @Positive Long id) {
        log.info("DELETE /api/habits/{} - Deleting habit", id);
        habitService.deleteHabit(id);
        log.info("Habit with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}

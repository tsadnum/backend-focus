package com.app.focus.controller;

import com.app.focus.dto.habit.HabitLogRequestDTO;
import com.app.focus.dto.habit.HabitLogResponseDTO;
import com.app.focus.interfaces.IHabitLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/habit-logs")
@RequiredArgsConstructor
@Slf4j
public class HabitLogController {

    private final IHabitLogService habitLogService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<HabitLogResponseDTO> logHabit(@RequestBody @Valid HabitLogRequestDTO dto) {
        log.info("POST /api/habit-logs - Logging habit ID {} at {}", dto.getHabitId(), dto.getCompletionTime());
        HabitLogResponseDTO createdLog = habitLogService.saveLog(dto);
        log.info("Habit log created for habit ID {} on {}", createdLog.getHabitId(), createdLog.getCompletionTime());
        URI location = URI.create("/api/habit-logs/" + createdLog.getId());
        return ResponseEntity.created(location).body(createdLog); // 201 Created + Location
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<HabitLogResponseDTO>> getLogsForUser() {
        log.info("GET /api/habit-logs - Fetching all habit logs for authenticated user");
        List<HabitLogResponseDTO> logs = habitLogService.getLogsForUser();
        log.debug("Number of habit logs retrieved: {}", logs.size());
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/by-date")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<HabitLogResponseDTO>> getByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("GET /api/habit-logs/by-date - Fetching habit logs for date: {}", date);
        List<HabitLogResponseDTO> logs = habitLogService.getLogsByDate(date);
        log.debug("Logs retrieved for {}: {}", date, logs.size());
        return ResponseEntity.ok(logs);
    }
}

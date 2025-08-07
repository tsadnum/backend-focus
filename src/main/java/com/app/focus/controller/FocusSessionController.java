package com.app.focus.controller;

import com.app.focus.dto.focusSession.FocusSessionRequestDTO;
import com.app.focus.dto.focusSession.FocusSessionResponseDTO;
import com.app.focus.interfaces.IFocusSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/focus-sessions")
@RequiredArgsConstructor
@Slf4j
public class FocusSessionController {

    private final IFocusSessionService focusSessionService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FocusSessionResponseDTO> createSession(@RequestBody @Valid FocusSessionRequestDTO dto) {
        log.info("POST /api/focus-sessions - Attempting to create new focus session.");
        FocusSessionResponseDTO created = focusSessionService.createSession(dto);
        log.info("Focus session created successfully with ID: {}", created.getId());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<FocusSessionResponseDTO>> getSessions() {
        log.info("GET /api/focus-sessions - Retrieving all focus sessions for authenticated user.");
        List<FocusSessionResponseDTO> sessions = focusSessionService.getMySessions();
        log.debug("Total focus sessions retrieved: {}", sessions.size());
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/today")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<FocusSessionResponseDTO>> getTodaySessions() {
        log.info("GET /api/focus-sessions/today - Retrieving today's focus sessions for authenticated user.");
        List<FocusSessionResponseDTO> todaySessions = focusSessionService.getTodaySessions();
        log.debug("Today's focus sessions retrieved: {}", todaySessions.size());
        return ResponseEntity.ok(todaySessions);
    }
}

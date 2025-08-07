package com.app.focus.controller;

import com.app.focus.dto.event.EventRequestDTO;
import com.app.focus.dto.event.EventResponseDTO;
import com.app.focus.interfaces.IEventService;
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
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final IEventService eventService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody @Valid EventRequestDTO dto) {
        log.info("POST /api/events - Creating new event");
        log.debug("Received payload: {}", dto);
        EventResponseDTO response = eventService.createEvent(dto);
        log.info("Event created with ID: {}", response.getId());
        return ResponseEntity.created(URI.create("/events/" + response.getId())).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<EventResponseDTO>> getUserEvents() {
        log.info("GET /api/events - Listing user events");
        List<EventResponseDTO> events = eventService.getUserEvents();
        log.debug("Events found: {}", events.size());
        return ResponseEntity.ok(events);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<EventResponseDTO> updateEvent(@PathVariable @Positive Long id,
                                                        @RequestBody @Valid EventRequestDTO dto) {
        log.info("PUT /api/events/{} - Updating event", id);
        log.debug("Update payload: {}", dto);
        EventResponseDTO updated = eventService.updateEvent(id, dto);
        log.info("Event updated successfully");
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable @Positive Long id) {
        log.info("DELETE /api/events/{} - Deleting event", id);
        eventService.deleteEvent(id);
        log.info("Event deleted successfully");
        return ResponseEntity.noContent().build();
    }
}

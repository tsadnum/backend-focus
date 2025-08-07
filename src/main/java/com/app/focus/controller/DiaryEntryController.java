package com.app.focus.controller;

import com.app.focus.dto.diary.DiaryEntryRequestDTO;
import com.app.focus.dto.diary.DiaryEntryResponseDTO;
import com.app.focus.interfaces.IDiaryEntryService;
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
@RequestMapping("/api/diary")
@RequiredArgsConstructor
@Slf4j
public class DiaryEntryController {

    private final IDiaryEntryService diaryEntryService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DiaryEntryResponseDTO> createEntry(@RequestBody @Valid DiaryEntryRequestDTO dto) {
        log.info("POST /api/diary - Creating new diary entry");
        DiaryEntryResponseDTO response = diaryEntryService.createEntry(dto);
        log.info("Diary entry created with ID: {}", response.getId());
        URI location = URI.create("/api/diary/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }


    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<DiaryEntryResponseDTO>> getUserEntries() {
        log.info("GET /api/diary - Listing user diary entries");
        List<DiaryEntryResponseDTO> entries = diaryEntryService.getUserEntries();
        log.debug("Entries found: {}", entries.size());
        return ResponseEntity.ok(entries);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DiaryEntryResponseDTO> updateEntry(@PathVariable @Positive Long id,
                                                             @RequestBody @Valid DiaryEntryRequestDTO dto) {
        log.info("PUT /api/diary/{} - Updating entry", id);
        log.debug("Update payload: {}", dto);
        DiaryEntryResponseDTO updated = diaryEntryService.updateEntry(id, dto);
        log.info("Diary entry updated successfully");
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEntry(@PathVariable @Positive Long id) {
        log.info("DELETE /api/diary/{} - Deleting entry", id);
        diaryEntryService.deleteEntry(id);
        log.info("Diary entry deleted successfully");
        return ResponseEntity.noContent().build();
    }
}

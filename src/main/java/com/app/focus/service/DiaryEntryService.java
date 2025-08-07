package com.app.focus.service;

import com.app.focus.dto.diary.DiaryEntryRequestDTO;
import com.app.focus.dto.diary.DiaryEntryResponseDTO;
import com.app.focus.entity.DiaryEntry;
import com.app.focus.entity.User;
import com.app.focus.interfaces.IDiaryEntryService;
import com.app.focus.repository.DiaryEntryRepository;
import com.app.focus.security.AuthenticatedUserProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryEntryService implements IDiaryEntryService {

    private final DiaryEntryRepository diaryEntryRepository;
    private final ModelMapper modelMapper;
    private final AuthenticatedUserProvider authUserProvider;

    public DiaryEntryResponseDTO createEntry(DiaryEntryRequestDTO dto) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Creating diary entry for user: {}", user.getEmail());

        DiaryEntry entry = modelMapper.map(dto, DiaryEntry.class);
        entry.setUser(user);

        DiaryEntry saved = diaryEntryRepository.save(entry);
        log.info("Diary entry created successfully with ID: {}", saved.getId());

        return modelMapper.map(saved, DiaryEntryResponseDTO.class);
    }

    public List<DiaryEntryResponseDTO> getUserEntries() {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Retrieving diary entries for user: {}", user.getEmail());

        List<DiaryEntry> entries = diaryEntryRepository.findByUser(user);
        log.info("Found {} diary entries for user: {}", entries.size(), user.getEmail());

        return entries.stream()
                .map(entry -> modelMapper.map(entry, DiaryEntryResponseDTO.class))
                .collect(Collectors.toList());
    }

    public DiaryEntryResponseDTO updateEntry(Long id, DiaryEntryRequestDTO dto) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Updating diary entry with ID: {} for user: {}", id, user.getEmail());

        DiaryEntry existing = diaryEntryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Diary entry not found with ID: {}", id);
                    return new EntityNotFoundException("Diary entry not found");
                });

        if (!existing.getUser().getId().equals(user.getId())) {
            log.warn("Access denied: User {} attempted to update entry ID: {}", user.getEmail(), id);
            throw new AccessDeniedException("Not authorized to update this entry");
        }

        modelMapper.map(dto, existing);
        DiaryEntry updated = diaryEntryRepository.save(existing);

        log.info("Diary entry updated successfully with ID: {}", updated.getId());
        return modelMapper.map(updated, DiaryEntryResponseDTO.class);
    }

    public void deleteEntry(Long id) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Deleting diary entry with ID: {} for user: {}", id, user.getEmail());

        DiaryEntry entry = diaryEntryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Diary entry not found with ID: {}", id);
                    return new EntityNotFoundException("Diary entry not found");
                });

        if (!entry.getUser().getId().equals(user.getId())) {
            log.warn("Access denied: User {} attempted to delete entry ID: {}", user.getEmail(), id);
            throw new AccessDeniedException("Not authorized to delete this entry");
        }

        diaryEntryRepository.delete(entry);
        log.info("Diary entry with ID: {} deleted successfully.", id);
    }
}

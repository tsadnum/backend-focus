package com.app.focus.interfaces;

import com.app.focus.dto.diary.DiaryEntryRequestDTO;
import com.app.focus.dto.diary.DiaryEntryResponseDTO;
import java.util.List;

public interface IDiaryEntryService {
    DiaryEntryResponseDTO createEntry(DiaryEntryRequestDTO dto);
    List<DiaryEntryResponseDTO> getUserEntries();
    DiaryEntryResponseDTO updateEntry(Long id, DiaryEntryRequestDTO dto);
    void deleteEntry(Long id);
}

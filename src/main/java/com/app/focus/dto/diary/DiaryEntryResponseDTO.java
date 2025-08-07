package com.app.focus.dto.diary;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DiaryEntryResponseDTO {

    private Long id;

    private String title;

    private String content;

    private LocalDate entryDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

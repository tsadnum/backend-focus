package com.app.focus.dto.diary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DiaryEntryRequestDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private LocalDate entryDate;
}

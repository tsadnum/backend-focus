package com.app.focus.dto.goal;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GoalResponseDTO {

    private Long id;

    private String title;

    private String description;

    private Double progress;

    private LocalDate targetDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

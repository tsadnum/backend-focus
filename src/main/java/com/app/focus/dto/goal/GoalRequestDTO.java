package com.app.focus.dto.goal;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GoalRequestDTO {

    @NotBlank
    private String title;

    private String description;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private Double progress;

    @NotNull
    private LocalDate targetDate;
}

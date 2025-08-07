package com.app.focus.dto.focusSession;

import com.app.focus.entity.enums.TimerMode;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FocusSessionRequestDTO {

    @NotNull
    private LocalDate sessionDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private TimerMode timerMode;
}

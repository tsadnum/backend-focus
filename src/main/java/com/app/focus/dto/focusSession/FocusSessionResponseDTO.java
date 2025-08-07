package com.app.focus.dto.focusSession;
import com.app.focus.entity.enums.TimerMode;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FocusSessionResponseDTO {

    private Long id;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private TimerMode timerMode;
}


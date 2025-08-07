package com.app.focus.dto.user;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserFilterRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}

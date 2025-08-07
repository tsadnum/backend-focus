package com.app.focus.dto.habit;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class HabitRequestDTO {
    private String name;
    private String description;
    private List<String> activeDays;
    private List<LocalTime> reminderTimes;
    private boolean isActive;
    private String icon;

}

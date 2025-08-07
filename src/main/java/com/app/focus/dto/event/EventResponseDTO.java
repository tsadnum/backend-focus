package com.app.focus.dto.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponseDTO {

    private Long id;

    private String title;

    private String description;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private String location;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

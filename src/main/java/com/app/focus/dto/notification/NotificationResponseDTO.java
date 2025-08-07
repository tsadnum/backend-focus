package com.app.focus.dto.notification;

import com.app.focus.entity.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDTO {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private LocalDateTime sentAt;
    private boolean isRead;
}

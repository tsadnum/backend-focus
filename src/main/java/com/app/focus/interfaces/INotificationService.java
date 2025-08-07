package com.app.focus.interfaces;

import com.app.focus.dto.notification.NotificationResponseDTO;
import com.app.focus.entity.enums.NotificationType;

import java.time.LocalDate;
import java.util.List;

public interface INotificationService {
    List<NotificationResponseDTO> getUserNotifications();
    void markAsRead(Long notificationId);
    long getUnreadCount();
    void createNotification(Long userId, String title, String message, NotificationType type);
    boolean existsSimilarNotification(Long userId, String title, String message, LocalDate date);


}

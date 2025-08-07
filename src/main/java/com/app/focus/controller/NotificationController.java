package com.app.focus.controller;

import com.app.focus.dto.notification.NotificationResponseDTO;
import com.app.focus.interfaces.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final INotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<NotificationResponseDTO>> getUserNotifications() {
        log.info("GET /api/notifications - Retrieving notifications for authenticated user");
        List<NotificationResponseDTO> notifications = notificationService.getUserNotifications();
        log.debug("Notifications retrieved: {}", notifications.size());
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{id}/mark-as-read")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        log.info("PATCH /api/notifications/{}/mark-as-read - Marking notification as read", id);
        notificationService.markAsRead(id);
        log.info("Notification with ID {} marked as read", id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Long> getUnreadCount() {
        log.info("GET /api/notifications/unread-count - Retrieving unread notification count");
        long count = notificationService.getUnreadCount();
        log.debug("Unread notification count: {}", count);
        return ResponseEntity.ok(count);
    }
}

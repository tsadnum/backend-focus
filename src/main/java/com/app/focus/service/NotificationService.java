package com.app.focus.service;

import com.app.focus.dto.notification.NotificationResponseDTO;
import com.app.focus.entity.Notification;
import com.app.focus.entity.enums.NotificationType;
import com.app.focus.interfaces.INotificationService;
import com.app.focus.repository.NotificationRepository;
import com.app.focus.repository.UserRepository;
import com.app.focus.security.AuthenticatedUserProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<NotificationResponseDTO> getUserNotifications() {
        var user = authenticatedUserProvider.getAuthenticatedUser();
        log.info("Fetching notifications for user with ID {}", user.getId());
        List<NotificationResponseDTO> notifications = notificationRepository.findByUserOrderBySentAtDesc(user).stream()
                .map(notification -> modelMapper.map(notification, NotificationResponseDTO.class))
                .collect(Collectors.toList());
        log.debug("User {} has {} notifications", user.getEmail(), notifications.size());
        return notifications;
    }

    @Override
    public void markAsRead(Long notificationId) {
        var user = authenticatedUserProvider.getAuthenticatedUser();
        log.info("Marking notification ID {} as read for user ID {}", notificationId, user.getId());

        var notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to access this notification");
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
            log.debug("Notification {} marked as read", notificationId);
        } else {
            log.debug("Notification {} was already marked as read", notificationId);
        }
    }


    @Override
    public long getUnreadCount() {
        var user = authenticatedUserProvider.getAuthenticatedUser();
        log.info("Getting unread notifications count for user ID {}", user.getId());
        long count = notificationRepository.findByUserOrderBySentAtDesc(user).stream()
                .filter(notification -> !notification.isRead())
                .count();
        log.debug("User {} has {} unread notifications", user.getEmail(), count);
        return count;
    }

    @Override
    public void createNotification(Long userId, String title, String message, NotificationType type) {
        log.info("Creating notification for user ID {} with title '{}'", userId, title);
        Notification notification = new Notification();
        notification.setUser(userRepository.getReferenceById(userId));
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setSentAt(LocalDateTime.now());
        notification.setRead(false);
        notificationRepository.save(notification);
        log.debug("Notification saved for user ID {}: {}", userId, title);
    }

    @Override
    public boolean existsSimilarNotification(Long userId, String title, String message, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        boolean exists = notificationRepository.existsByUserAndTitleAndMessageAndDate(userId, title, message, startOfDay);
        log.debug("Checking for similar notification for user ID {} on {}: exists = {}", userId, date, exists);
        return exists;
    }
}

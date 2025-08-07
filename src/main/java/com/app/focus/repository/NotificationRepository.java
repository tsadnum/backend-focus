package com.app.focus.repository;

import com.app.focus.entity.Notification;
import com.app.focus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderBySentAtDesc(User user);

    @Query("SELECT COUNT(n) > 0 FROM Notification n " +
            "WHERE n.user.id = :userId " +
            "AND n.title = :title " +
            "AND n.message = :message " +
            "AND DATE(n.sentAt) = :date")
    boolean existsByUserAndTitleAndMessageAndDate(
            @Param("userId") Long userId,
            @Param("title") String title,
            @Param("message") String message,
            @Param("date") LocalDateTime dateStartOfDay
    );
}

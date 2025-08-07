package com.app.focus.repository;

import com.app.focus.entity.FocusSession;
import com.app.focus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {
    List<FocusSession> findByUserIdOrderBySessionDateDesc(Long userId);
    List<FocusSession> findByUserAndSessionDate(User user, LocalDate sessionDate);
}

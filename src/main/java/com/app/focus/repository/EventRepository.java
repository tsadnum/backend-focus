package com.app.focus.repository;

import com.app.focus.entity.Event;
import com.app.focus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUser(User user);
    List<Event> findByUserId(Long userId);
}

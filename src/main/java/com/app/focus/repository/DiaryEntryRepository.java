package com.app.focus.repository;

import com.app.focus.entity.DiaryEntry;
import com.app.focus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Long> {
    List<DiaryEntry> findByUser(User user);
}

package com.app.focus.repository;

import com.app.focus.entity.Habit;
import com.app.focus.entity.HabitLog;
import com.app.focus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    @Query("SELECT h FROM HabitLog h WHERE h.habit = :habit AND DATE(h.completionTime) = :date")
    List<HabitLog> findByHabitAndDate(@Param("habit") Habit habit, @Param("date") LocalDate date);

    @Query("SELECT h FROM HabitLog h WHERE h.habit.user = :user AND DATE(h.completionTime) = :date")
    List<HabitLog> findByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);

    List<HabitLog> findByHabit_User(User user);

    @Query("""
    SELECT COUNT(h) > 0 FROM HabitLog h 
    WHERE h.habit.id = :habitId 
    AND h.isCompleted = true 
    AND h.completionTime BETWEEN :startOfDay AND :endOfDay
""")
    boolean existsCompletedLogToday(
            @Param("habitId") Long habitId,
            @Param("startOfDay") java.time.LocalDateTime startOfDay,
            @Param("endOfDay") java.time.LocalDateTime endOfDay);

}

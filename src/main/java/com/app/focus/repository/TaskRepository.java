package com.app.focus.repository;

import com.app.focus.entity.Task;
import com.app.focus.entity.User;
import com.app.focus.entity.enums.TaskStatus;
import com.app.focus.entity.enums.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    List<Task> findByUserAndType(User user, TaskType type);
    List<Task> findByParentTaskId(Long parentTaskId);
    List<Task> findByUserIdAndStatusOrderByKanbanOrderAsc(Long userId, TaskStatus status);
    List<Task> findByUserIdOrderByDueDateAsc(Long userId);
    long countByType(TaskType type);

}

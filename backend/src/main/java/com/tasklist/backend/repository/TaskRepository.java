package com.tasklist.backend.repository;

import com.tasklist.backend.entity.Task;
import com.tasklist.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserOrderByCreatedAtDesc(User user);
}
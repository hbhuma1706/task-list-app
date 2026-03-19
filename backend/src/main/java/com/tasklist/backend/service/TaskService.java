package com.tasklist.backend.service;

import com.tasklist.backend.dto.*;
import com.tasklist.backend.entity.Task;
import com.tasklist.backend.entity.User;
import com.tasklist.backend.repository.TaskRepository;
import com.tasklist.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private TaskResponse toResponse(Task task) {
        TaskResponse r = new TaskResponse();
        r.setId(task.getId());
        r.setTitle(task.getTitle());
        r.setDescription(task.getDescription());
        r.setCompleted(task.isCompleted());
        r.setCreatedAt(task.getCreatedAt());
        r.setDueDate(task.getDueDate());
        r.setOverdue(task.getDueDate() != null
                && !task.isCompleted()
                && task.getDueDate().isBefore(LocalDateTime.now()));
        return r;
    }

    public List<TaskResponse> getTasks(String username) {
        return taskRepository.findByUserOrderByCreatedAtDesc(getUser(username))
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TaskResponse createTask(String username, TaskRequest request) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(request.isCompleted())
                .dueDate(request.getDueDate())
                .user(getUser(username))
                .build();
        return toResponse(taskRepository.save(task));
    }

    public TaskResponse updateTask(String username, Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Forbidden");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());
        task.setDueDate(request.getDueDate());
        return toResponse(taskRepository.save(task));
    }

    public void deleteTask(String username, Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Forbidden");
        }

        taskRepository.delete(task);
    }
}

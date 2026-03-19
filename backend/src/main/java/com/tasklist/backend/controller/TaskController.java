package com.tasklist.backend.controller;

import com.tasklist.backend.dto.*;
import com.tasklist.backend.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(Principal principal) {
        return ResponseEntity.ok(taskService.getTasks(principal.getName()));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(Principal principal,
                                                   @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(principal.getName(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(Principal principal,
                                                   @PathVariable Long id,
                                                   @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(principal.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(Principal principal, @PathVariable Long id) {
        taskService.deleteTask(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}

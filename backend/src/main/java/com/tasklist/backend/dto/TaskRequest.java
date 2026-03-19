package com.tasklist.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime dueDate;
}
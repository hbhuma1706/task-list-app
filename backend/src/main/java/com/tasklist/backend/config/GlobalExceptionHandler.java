package com.tasklist.backend.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("message", "Validation failed");
        response.put("errors", fieldErrors);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex.getMessage().equals("Invalid credentials")) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (ex.getMessage().equals("Task not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex.getMessage().equals("Forbidden")) {
            status = HttpStatus.FORBIDDEN;
        } else if (ex.getMessage().equals("Invalid refresh token")
                || ex.getMessage().equals("Refresh token not found")
                || ex.getMessage().equals("Refresh token has been revoked")
                || ex.getMessage().equals("Refresh token has expired")) {
            status = HttpStatus.UNAUTHORIZED;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", status.value());
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(status).body(response);
    }
}

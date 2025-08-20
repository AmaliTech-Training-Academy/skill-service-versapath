package com.capstone.skill_service.exception;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Hidden
public class GlobalException {

    @ExceptionHandler(TagExistsException.class)
    public ResponseEntity<?> handleTagExists
            (TagExistsException exception, WebRequest webRequest) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", HttpStatus.CONFLICT.value());
        body.put("timestamp", LocalDateTime.now());
        body.put("message", exception.getMessage());
        body.put("path", webRequest.getContextPath());
        body.put("sessionId", webRequest.getSessionId());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<?> handleTagNotFound(
            TagNotFoundException exception, WebRequest webRequest) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", HttpStatus.NOT_FOUND.value());
        body.put("timestamp", LocalDateTime.now());
        body.put("message", exception.getMessage());
        body.put("path", webRequest.getContextPath());
        body.put("sessionId", webRequest.getSessionId());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException
            (Exception exception, WebRequest webRequest) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", HttpStatus.BAD_REQUEST.value());
        body.put("timestamp", LocalDateTime.now());
        body.put("message", exception.getMessage());
        body.put("path", webRequest.getContextPath());
        body.put("sessionId", webRequest.getSessionId());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Error.class)
    public ResponseEntity<?> handleError
            (Exception exception, WebRequest webRequest) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("timestamp", LocalDateTime.now());
        body.put("message", exception.getMessage());
        body.put("path", webRequest.getContextPath());
        body.put("sessionId", webRequest.getSessionId());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handle404(NoHandlerFoundException ex) {
        Map<String, Object> body = Map.of(
                "status", 404,
                "error", "Not Found",
                "path", ex.getRequestURL()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(AccessDeniedException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("Error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();

            errors.computeIfAbsent(field, key -> new ArrayList<>()).add(message);
        });

        return ResponseEntity.badRequest().body(errors);
    }
}

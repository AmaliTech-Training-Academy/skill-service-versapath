package com.capstone.skill_service.exception;

import com.capstone.skill_service.dto.ClientResponseFormValidationErrorDto;
import com.capstone.skill_service.dto.ClientResponseFormatDto;
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
            (TagExistsException exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(false)
                .message("Tag Error!")
                .errors(List.of(Map.of("message", exception.getMessage())))
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ClusterExistsException.class)
    public ResponseEntity<?> handleClusterExists
            (ClusterExistsException exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(false)
                .message("Cluster Error!")
                .errors(List.of(Map.of("message", exception.getMessage())))
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<?> handleTagNotFound(
            TagNotFoundException exception) {
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(false)
                .message("Tag Error!")
                .errors(List.of(Map.of("message", exception.getMessage())))
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ClusterNotFoundException.class)
    public ResponseEntity<?> handleClusterNotFound(
            ClusterNotFoundException exception) {
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(false)
                .message("Cluster Error!")
                .errors(List.of(Map.of("message", exception.getMessage())))
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException
            (Exception exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(false)
                .message("Unexpected Exception")
                .errors(List.of(Map.of("message", exception.getMessage())))
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Error.class)
    public ResponseEntity<?> handleError
            (Exception exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(false)
                .message("Internal Server Error!")
                .errors(List.of(Map.of("message", exception.getMessage())))
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handle404(NoHandlerFoundException exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(false)
                .message("Page/url no found")
                .errors(List.of(Map.of("message", exception.getMessage())))
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleUnauthorized(AccessDeniedException exception) {
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(false)
                .message("Unauthorized Error!")
                .errors(List.of(Map.of("message", exception.getMessage())))
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        /* group all the validation error into a respective input field
        ex: {
             "name":[error1,error2],
             "email":[error1,error2]
            }
        */
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();

            errors.computeIfAbsent(field, key -> new ArrayList<>()).add(message);
        });

        ClientResponseFormValidationErrorDto response = ClientResponseFormValidationErrorDto.builder()
                .status(false)
                .message("Form validation Error!")
                .errors(List.of(errors))
                .data(null)
                .build();

        return ResponseEntity.badRequest().body(response);
    }
}

package com.capstone.skill_service.exception;

import com.capstone.skill_service.dto.ClientResponseFormValidationErrorDto;
import com.capstone.skill_service.dto.ClientResponseFormatDto;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
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
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ClusterExistsException.class)
    public ResponseEntity<?> handleClusterExists
            (ClusterExistsException exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<?> handleTagNotFound(
            TagNotFoundException exception) {
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ClusterNotFoundException.class)
    public ResponseEntity<?> handleClusterNotFound(
            ClusterNotFoundException exception) {
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException
            (Exception exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Error.class)
    public ResponseEntity<?> handleError
            (Exception exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handle404(NoHandlerFoundException exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleUnauthorized(AccessDeniedException exception) {
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
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
        List<String> listOfErrors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            listOfErrors.add(message);

            errors.computeIfAbsent(field, key -> new ArrayList<>()).add(message);
        });

        ClientResponseFormValidationErrorDto response = ClientResponseFormValidationErrorDto.builder()
                .success(false)
                .message("Form validation Error!")
                .errors(listOfErrors)
                .data(null)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AtomNotFoundException.class)
    public ResponseEntity<?> handleAtomNotFound(
            AtomNotFoundException exception) {
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AtomExistsException.class)
    public ResponseEntity<?> handleAtomExists
            (AtomExistsException exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CapsuleNotFoundException.class)
    public ResponseEntity<?> handleCapsuleNotFound(
            CapsuleNotFoundException exception) {
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CapsuleExistsException.class)
    public ResponseEntity<?> handleCapsuleExists
            (CapsuleExistsException exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AlreadyAssignedException.class)
    public ResponseEntity<?> handleAlreadyAssignedException
            (AlreadyAssignedException exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidAtomIdsException.class)
    public ResponseEntity<?> handleInvalidAtomIdsException
            (InvalidAtomIdsException exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TrackNotFoundException.class)
    public ResponseEntity<?> handleTrackNotFound(
            TrackNotFoundException exception) {
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TrackExistsException.class)
    public ResponseEntity<?> handleTrackExists
            (TrackExistsException exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RouteExistsException.class)
    public ResponseEntity<?> handleRouteExists
            (RouteExistsException exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<?> handleRouteNotFound(
            RouteNotFoundException exception) {
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCapsuleIdsException.class)
    public ResponseEntity<?> handleInvalidCapsuleIdsException
            (InvalidCapsuleIdsException exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTrackIdsException.class)
    public ResponseEntity<?> handleInvalidTrackIdsException
            (InvalidTrackIdsException exception) {

        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<?> handleFileException(FileException exception) {
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(HeaderException.class)
    public ResponseEntity<?> handleHeaderException(HeaderException exception) {
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(false)
                .message(exception.getMessage())
                .errors(null)
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(response);
    }
}

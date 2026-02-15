package ru.sinvic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ContentNotFoundException.class, SessionNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleEntityNotFound(
        EntityNotFoundException ex, WebRequest request) {
        Objects.requireNonNull(request, "WebRequest must not be null");

        ErrorResponse responseBody = ErrorResponse.builder()
            .code(ex.getClass().getSimpleName())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .path(request.getContextPath())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
        MethodArgumentNotValidException ex, WebRequest request) {
        Objects.requireNonNull(request, "WebRequest must not be null");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> {
            errors.put(err.getField(), err.getDefaultMessage());
        });

        ErrorResponse responseBody = ErrorResponse.builder()
            .code(ex.getClass().getSimpleName())
            .message(ex.getMessage())
            .errorData(errors)
            .timestamp(LocalDateTime.now())
            .path(request.getContextPath())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGenericError(
        RuntimeException ex) {

        ErrorResponse responseBody = ErrorResponse.builder()
            .code(ex.getClass().getSimpleName())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
    }
}

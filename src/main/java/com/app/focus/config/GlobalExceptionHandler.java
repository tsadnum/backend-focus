package com.app.focus.config;

import com.app.focus.entity.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String traceId = UUID.randomUUID().toString();

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (a, b) -> a
                ));

        log.warn("Validation failed [traceId={}]: {}", traceId, errors);

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Failed",
                "Some fields are invalid", errors, request, traceId);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        String traceId = UUID.randomUUID().toString();

        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString().replaceAll("^.*\\.", ""),
                        v -> v.getMessage(),
                        (a, b) -> a
                ));

        log.warn("Constraint violation [traceId={}]: {}", traceId, errors);

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Constraint Violation",
                "Invalid input data", errors, request, traceId);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {

        String traceId = UUID.randomUUID().toString();
        log.warn("Bad credentials [traceId={}]: {}", traceId, ex.getMessage());

        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized",
                "Invalid credentials", null, request, traceId);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        String traceId = UUID.randomUUID().toString();
        log.warn("Access denied [traceId={}]: {}", traceId, ex.getMessage());

        return buildErrorResponse(HttpStatus.FORBIDDEN, "Forbidden",
                "Access denied", null, request, traceId);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntime(
            RuntimeException ex, HttpServletRequest request) {

        String traceId = UUID.randomUUID().toString();
        log.error("Runtime exception [traceId={}]: {}", traceId, ex.getMessage(), ex);

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Runtime Exception",
                ex.getMessage(), null, request, traceId);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {

        String traceId = UUID.randomUUID().toString();
        log.error("Unhandled exception [traceId={}]: {}", traceId, ex.getMessage(), ex);

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred", null, request, traceId);
    }

    private static ResponseEntity<ApiErrorResponse> buildErrorResponse(
            HttpStatus status, String error, String message,
            Map<String, String> fieldErrors, HttpServletRequest request, String traceId) {

        ApiErrorResponse response = new ApiErrorResponse();
        response.setStatus(status.value());
        response.setError(error);
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        response.setFieldErrors(fieldErrors);
        response.setTraceId(traceId);
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, status);
    }
}

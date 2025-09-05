package com.waduclay.springaitutorial.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler that provides centralized exception handling across the application.
 * This class uses Spring's {@code @ControllerAdvice} to handle exceptions thrown by any controller
 * and converts them into appropriate HTTP responses with consistent error formatting.
 * All exceptions are logged with appropriate log levels and returned as structured JSON responses.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handles AIServiceException thrown by AI-related operations.
     * Maps AI service failures to HTTP 503 (Service Unavailable) status and logs the error.
     * 
     * @param ex the AIServiceException that was thrown
     * @return ResponseEntity with error details and HTTP 503 status
     */
    @ExceptionHandler(AIServiceException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<Map<String, Object>> handleAIServiceException(AIServiceException ex) {
        log.error("AI Service error occurred: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "AI Service Error", ex.getMessage());
    }
    
    /**
     * Handles VectorStoreException thrown by vector store operations.
     * Maps vector store failures to HTTP 500 (Internal Server Error) status and logs the error.
     * 
     * @param ex the VectorStoreException that was thrown
     * @return ResponseEntity with error details and HTTP 500 status
     */
    @ExceptionHandler(VectorStoreException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, Object>> handleVectorStoreException(VectorStoreException ex) {
        log.error("Vector Store error occurred: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Vector Store Error", ex.getMessage());
    }
    
    /**
     * Handles ConstraintViolationException from Bean Validation failures.
     * Maps validation constraint violations to HTTP 400 (Bad Request) status.
     * 
     * @param ex the ConstraintViolationException that was thrown
     * @return ResponseEntity with validation error details and HTTP 400 status
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Validation error occurred: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", ex.getMessage());
    }
    
    /**
     * Handles MethodArgumentNotValidException from request body validation failures.
     * Collects all field validation errors and returns them as a consolidated message.
     * 
     * @param ex the MethodArgumentNotValidException that was thrown
     * @return ResponseEntity with field validation errors and HTTP 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("Method argument validation error occurred: {}", ex.getMessage());
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errorMessage.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
        );
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", errorMessage.toString());
    }
    
    /**
     * Handles MethodArgumentTypeMismatchException from parameter type conversion failures.
     * Provides clear information about the expected parameter type and received value.
     * 
     * @param ex the MethodArgumentTypeMismatchException that was thrown
     * @return ResponseEntity with type mismatch error details and HTTP 400 status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("Method argument type mismatch error occurred: {}", ex.getMessage());
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s", 
            ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Type Mismatch Error", message);
    }
    
    /**
     * Handles all other uncaught exceptions as a fallback.
     * Maps any unhandled exception to HTTP 500 (Internal Server Error) with a generic message
     * to avoid exposing sensitive system information.
     * 
     * @param ex the generic Exception that was thrown
     * @return ResponseEntity with generic error message and HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred");
    }
    
    /**
     * Builds a standardized error response map with consistent structure.
     * Creates a response containing timestamp, status code, error type, and message.
     * 
     * @param status the HTTP status to return
     * @param error the error type description
     * @param message the detailed error message
     * @return ResponseEntity containing the structured error response
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        return new ResponseEntity<>(errorResponse, status);
    }
}

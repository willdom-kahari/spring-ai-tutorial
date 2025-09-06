package com.waduclay.springaitutorial.exception;

import com.waduclay.springaitutorial.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;

import java.util.Objects;

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
    public ApiResponse<String> handleAIServiceException(AIServiceException ex) {
        log.error("AI Service error occurred: {}", ex.getMessage(), ex);
        return new ApiResponse<>(false, "AI Service Error", ex.getMessage());
    }
    
    /**
     * Handles VectorStoreException thrown by vector store operations.
     * Maps vector store failures to HTTP 500 (Internal Server Error) status and logs the error.
     * 
     * @param ex the VectorStoreException that was thrown
     * @return ResponseEntity with error details and HTTP 500 status
     */
    @ExceptionHandler(VectorStoreException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ApiResponse<String> handleVectorStoreException(VectorStoreException ex) {
        log.error("Vector Store error occurred: {}", ex.getMessage(), ex);
        return new ApiResponse<>(false, "Vector Store Error", ex.getMessage());
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
    public ApiResponse<String> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Validation error occurred: {}", ex.getMessage());
        return new ApiResponse<>(false, "Validation Error", ex.getMessage());
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
    public ApiResponse<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("Method argument validation error occurred: {}", ex.getMessage());
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errorMessage.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
        );
        return new ApiResponse<>(false, "Validation Error", errorMessage.toString());
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
    public ApiResponse<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("Method argument type mismatch error occurred: {}", ex.getMessage());
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s", 
            ex.getValue(), ex.getName(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
        return new ApiResponse<>(false, "Type Mismatch Error", message);
    }
    
    /**
     * Handles SecurityException from input sanitization and content filtering.
     * Maps security validation failures to HTTP 400 (Bad Request) status.
     * Returns masked error message to prevent information disclosure while logging full details.
     * 
     * @param ex the SecurityException that was thrown
     * @return ResponseEntity with masked security error and HTTP 400 status
     */
    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleSecurityException(SecurityException ex) {
        log.warn("Security validation failed: {}", ex.getMessage());
        // Return generic message to prevent information disclosure about security measures
        return new ApiResponse<>(false, "Request Validation Error", "Request contains invalid or inappropriate content");
    }
    
    /**
     * Handles IllegalArgumentException from basic input validation.
     * Maps argument validation failures to HTTP 400 (Bad Request) status.
     * 
     * @param ex the IllegalArgumentException that was thrown
     * @return ResponseEntity with validation error details and HTTP 400 status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Input validation error: {}", ex.getMessage());
        return new ApiResponse<>(false, "Input Validation Error", ex.getMessage());
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
    public ApiResponse<String> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return new ApiResponse<>(false, "Internal Server Error", "An unexpected error occurred");
    }
    

}

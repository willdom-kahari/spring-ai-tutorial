package com.waduclay.springaitutorial.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * Standardized API response wrapper for consistent response format across all endpoints.
 * This generic class provides a uniform structure for API responses, including success/error status,
 * data payload, messages, timestamps, and error details. It ensures all API endpoints return
 * responses in the same format for better client-side handling and debugging.
 * 
 * @param <T> the type of data contained in the response
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String error;

    /**
     * Default constructor that initializes the response with current timestamp.
     * Sets the timestamp to the current date and time when the response is created.
     */
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Creates a successful API response with data and default success message.
     * 
     * @param <T> the type of data in the response
     * @param data the response data payload
     * @return ApiResponse configured as successful with the provided data
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        response.message = "Request processed successfully";
        return response;
    }

    /**
     * Creates a successful API response with data and custom message.
     * 
     * @param <T> the type of data in the response
     * @param data the response data payload
     * @param message custom success message
     * @return ApiResponse configured as successful with the provided data and message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        response.message = message;
        return response;
    }

    /**
     * Creates an error API response with error message and default failure message.
     * 
     * @param <T> the type of data in the response (will be null for error responses)
     * @param errorMessage the error details
     * @return ApiResponse configured as failed with the provided error message
     */
    public static <T> ApiResponse<T> error(String errorMessage) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = errorMessage;
        response.message = "Request failed";
        return response;
    }

    /**
     * Creates an error API response with error message and custom failure message.
     * 
     * @param <T> the type of data in the response (will be null for error responses)
     * @param errorMessage the error details
     * @param message custom failure message
     * @return ApiResponse configured as failed with the provided error and message
     */
    public static <T> ApiResponse<T> error(String errorMessage, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = errorMessage;
        response.message = message;
        return response;
    }

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

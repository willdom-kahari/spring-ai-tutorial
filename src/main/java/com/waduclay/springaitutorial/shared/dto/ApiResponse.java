package com.waduclay.springaitutorial.shared.dto;

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
public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {


    /**
     * Creates a successful API response with data and default success message.
     * 
     * @param <T> the type of data in the response
     * @param data the response data payload
     * @return ApiResponse configured as successful with the provided data
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Request processed successfully", data);
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
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Creates an error API response with error message and default failure message.
     * 
     * @param <T> the type of data in the response (will be null for error responses)
     * @param errorMessage the error details
     * @return ApiResponse configured as failed with the provided error message
     */
    public static <T> ApiResponse<T> error(String errorMessage) {
        return new ApiResponse<>(false, "Request failed", (T) errorMessage);
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
        return new ApiResponse<>(false, message, (T) errorMessage);

    }
}

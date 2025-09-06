package com.waduclay.springaitutorial.shared.exception;

/**
 * Custom runtime exception for AI service related failures.
 * This exception is thrown when AI operations fail, such as chat completion failures,
 * prompt processing errors, or AI service connectivity issues. It provides a way to
 * distinguish AI-specific errors from other system exceptions for proper error handling.
 *
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
public class AIServiceException extends RuntimeException {

    /**
     * Constructs a new AIServiceException with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public AIServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new AIServiceException with the specified detail message and cause.
     * This constructor is useful for wrapping lower-level exceptions that occur
     * during AI service operations while preserving the original stack trace.
     *
     * @param message the detail message explaining the cause of the exception
     * @param cause   the cause of the exception (which is saved for later retrieval)
     */
    public AIServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.waduclay.springaitutorial.shared.exception;

/**
 * Custom runtime exception for vector store related failures.
 * This exception is thrown when vector store operations fail, such as similarity search errors,
 * document embedding failures, or vector database connectivity issues. It provides a way to
 * distinguish vector store-specific errors from other system exceptions for proper error handling.
 *
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
public class VectorStoreException extends RuntimeException {

    /**
     * Constructs a new VectorStoreException with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public VectorStoreException(String message) {
        super(message);
    }

    /**
     * Constructs a new VectorStoreException with the specified detail message and cause.
     * This constructor is useful for wrapping lower-level exceptions that occur
     * during vector store operations while preserving the original stack trace.
     *
     * @param message the detail message explaining the cause of the exception
     * @param cause   the cause of the exception (which is saved for later retrieval)
     */
    public VectorStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}

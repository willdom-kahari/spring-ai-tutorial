package com.waduclay.springaitutorial.exception;

/**
 * Custom exception for vector store related failures
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
public class VectorStoreException extends RuntimeException {
    
    public VectorStoreException(String message) {
        super(message);
    }
    
    public VectorStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}

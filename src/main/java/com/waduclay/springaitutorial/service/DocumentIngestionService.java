package com.waduclay.springaitutorial.service;

import com.waduclay.springaitutorial.dto.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Service interface for document ingestion and management operations.
 * This interface defines the contract for document processing functionality, providing
 * methods to ingest, process, store, and manage documents in the vector database for RAG operations.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
public interface DocumentIngestionService {
    
    /**
     * Ingests a document file by processing it into chunks and storing in vector database.
     * This method handles various document formats, extracts text content, splits it into
     * appropriate chunks, generates embeddings, and stores them for similarity search.
     * 
     * @param file the uploaded document file to process
     * @param metadata optional metadata to associate with the document
     * @return ApiResponse containing processing results and document information
     * @throws IOException if there's an error reading the uploaded file
     */
    ApiResponse<Map<String, Object>> ingestDocument(MultipartFile file, String metadata) throws IOException;
    
    /**
     * Ingests raw text content directly without file upload.
     * This method processes text content, splits it into appropriate chunks,
     * generates embeddings, and stores them in the vector database.
     * 
     * @param content the text content to process
     * @param title optional title for the document
     * @param metadata optional metadata to associate with the document
     * @return ApiResponse containing processing results and document information
     */
    ApiResponse<Map<String, Object>> ingestTextContent(String content, String title, String metadata);
    
    /**
     * Lists all documents currently stored in the vector database.
     * This method retrieves information about all stored documents including
     * their metadata, processing status, and basic statistics.
     * 
     * @return ApiResponse containing list of document information
     */
    ApiResponse<List<Map<String, Object>>> listDocuments();
    
    /**
     * Deletes specified documents from the vector store.
     * This method removes documents and their associated embeddings from the
     * vector database, freeing up storage space and removing outdated content.
     * 
     * @param documentIds comma-separated string of document IDs to delete
     * @return ApiResponse containing deletion results and statistics
     */
    ApiResponse<Map<String, Object>> deleteDocuments(String documentIds);
    
    /**
     * Retrieves comprehensive statistics about the document store.
     * This method provides metrics about the vector database including document count,
     * storage usage, processing performance, and other relevant statistics.
     * 
     * @return ApiResponse containing document store statistics
     */
    ApiResponse<Map<String, Object>> getDocumentStatistics();
    
    /**
     * Validates if a document file format is supported.
     * This method checks if the uploaded file format can be processed by the system.
     * 
     * @param filename the name of the file to validate
     * @return true if the file format is supported, false otherwise
     */
    boolean isSupportedFileFormat(String filename);
    
    /**
     * Extracts text content from a supported file format.
     * This method handles different file types and extracts readable text content
     * that can be processed for embedding generation.
     * 
     * @param file the uploaded file to extract text from
     * @return extracted text content from the file
     * @throws IOException if there's an error reading the file
     */
    String extractTextFromFile(MultipartFile file) throws IOException;
}

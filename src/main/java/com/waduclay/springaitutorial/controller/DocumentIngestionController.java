package com.waduclay.springaitutorial.controller;

import com.waduclay.springaitutorial.dto.ApiResponse;
import com.waduclay.springaitutorial.service.DocumentIngestionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * REST controller for document ingestion operations.
 * This controller provides endpoints for uploading, processing, and managing documents
 * in the vector store. It handles various document formats and provides comprehensive
 * document management capabilities for RAG operations.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@RestController
@RequestMapping("/api/documents")
@Validated
@Tag(name = "Document Management", description = "Document ingestion and management operations for RAG")
public class DocumentIngestionController {
    
    private final DocumentIngestionService documentIngestionService;
    
    /**
     * Constructs a new DocumentIngestionController with the provided DocumentIngestionService.
     * 
     * @param documentIngestionService the DocumentIngestionService for document operations
     */
    public DocumentIngestionController(DocumentIngestionService documentIngestionService) {
        this.documentIngestionService = documentIngestionService;
    }
    
    /**
     * Uploads and processes a document file for vector storage.
     * This endpoint accepts various document formats (TXT, PDF, MD), processes them into
     * chunks, generates embeddings, and stores them in the vector database for RAG operations.
     * 
     * @param file the document file to upload and process
     * @param metadata optional metadata to associate with the document
     * @return ApiResponse containing processing results and document information
     * @throws IOException if there's an error reading the uploaded file
     */
    @Operation(
        summary = "Upload Document",
        description = "Uploads a document file, processes it into chunks, generates embeddings, and stores in vector database"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Document successfully uploaded and processed"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Invalid file or parameters"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", 
            description = "Error processing document"
        )
    })
    @PostMapping("/upload")
    public ApiResponse<Map<String, Object>> uploadDocument(
            @Parameter(description = "Document file to upload", required = true)
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "Optional metadata for the document", example = "user-uploaded")
            @RequestParam(value = "metadata", required = false) String metadata
    ) throws IOException {
        return documentIngestionService.ingestDocument(file, metadata);
    }
    
    /**
     * Ingests text content directly without file upload.
     * This endpoint processes raw text content, splits it into appropriate chunks,
     * generates embeddings, and stores them in the vector database.
     * 
     * @param content the text content to process (1-10000 characters, cannot be blank)
     * @param title optional title for the document
     * @param metadata optional metadata to associate with the document
     * @return ApiResponse containing processing results and document information
     */
    @Operation(
        summary = "Ingest Text Content",
        description = "Processes raw text content directly, splits into chunks, and stores in vector database"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Text content successfully processed and stored"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Invalid content or parameters"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", 
            description = "Error processing content"
        )
    })
    @PostMapping("/ingest-text")
    public ApiResponse<Map<String, Object>> ingestTextContent(
            @Parameter(description = "Text content to process", required = true)
            @RequestParam("content") 
            @NotBlank(message = "Content cannot be blank")
            @Size(min = 1, max = 10000, message = "Content must be between 1 and 10000 characters")
            String content,
            
            @Parameter(description = "Optional title for the document", example = "User Document")
            @RequestParam(value = "title", required = false) String title,
            
            @Parameter(description = "Optional metadata for the document", example = "user-input")
            @RequestParam(value = "metadata", required = false) String metadata
    ) {
        return documentIngestionService.ingestTextContent(content, title, metadata);
    }
    
    /**
     * Lists all documents currently stored in the vector database.
     * This endpoint provides information about all documents including their metadata,
     * processing status, and basic statistics.
     * 
     * @return ApiResponse containing list of document information
     */
    @Operation(
        summary = "List Documents",
        description = "Retrieves information about all documents stored in the vector database"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved document list"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", 
            description = "Error retrieving documents"
        )
    })
    @GetMapping("/list")
    public ApiResponse<List<Map<String, Object>>> listDocuments() {
        return documentIngestionService.listDocuments();
    }
    
    /**
     * Deletes documents from the vector store by their IDs.
     * This endpoint removes specified documents and their associated embeddings
     * from the vector database, freeing up storage space.
     * 
     * @param documentIds comma-separated list of document IDs to delete
     * @return ApiResponse containing deletion results
     */
    @Operation(
        summary = "Delete Documents",
        description = "Removes specified documents and their embeddings from the vector database"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Documents successfully deleted"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Invalid document IDs"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", 
            description = "Error deleting documents"
        )
    })
    @DeleteMapping("/delete")
    public ApiResponse<Map<String, Object>> deleteDocuments(
            @Parameter(description = "Comma-separated document IDs to delete", example = "doc1,doc2,doc3")
            @RequestParam("ids") 
            @NotBlank(message = "Document IDs cannot be blank")
            String documentIds
    ) {
        return documentIngestionService.deleteDocuments(documentIds);
    }
    
    /**
     * Gets statistics about the document store.
     * This endpoint provides comprehensive statistics about the vector database
     * including document count, storage usage, and processing metrics.
     * 
     * @return ApiResponse containing document store statistics
     */
    @Operation(
        summary = "Get Document Statistics",
        description = "Retrieves comprehensive statistics about the vector database and stored documents"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved statistics"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", 
            description = "Error retrieving statistics"
        )
    })
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getDocumentStatistics() {
        return documentIngestionService.getDocumentStatistics();
    }
}

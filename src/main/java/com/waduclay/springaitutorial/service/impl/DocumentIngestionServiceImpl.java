package com.waduclay.springaitutorial.service.impl;

import com.waduclay.springaitutorial.dto.ApiResponse;
import com.waduclay.springaitutorial.exception.AIServiceException;
import com.waduclay.springaitutorial.exception.VectorStoreException;
import com.waduclay.springaitutorial.repository.VectorStoreRepository;
import com.waduclay.springaitutorial.service.DocumentIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of DocumentIngestionService for document processing and management.
 * This service handles the complete document ingestion pipeline including file upload,
 * text extraction, document chunking, embedding generation, and vector store operations.
 * It supports various file formats and provides comprehensive document management capabilities.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Service
public class DocumentIngestionServiceImpl implements DocumentIngestionService {
    
    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionServiceImpl.class);
    
    /** Default chunk size for document splitting */
    private static final int DEFAULT_CHUNK_SIZE = 800;
    
    /** Default overlap size for context preservation */
    private static final int DEFAULT_OVERLAP_SIZE = 400;
    
    /** Maximum file size allowed for upload (10MB) */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    /** Supported file extensions for document processing */
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
        "txt", "md", "markdown", "text", "log", "csv", "json", "xml", "html", "htm"
    );
    
    private final VectorStoreRepository vectorStoreRepository;
    
    /**
     * Constructs a new DocumentIngestionServiceImpl with required dependencies.
     * 
     * @param vectorStoreRepository repository for vector store operations
     */
    public DocumentIngestionServiceImpl(VectorStoreRepository vectorStoreRepository) {
        this.vectorStoreRepository = vectorStoreRepository;

    }
    
    @Override
    public ApiResponse<Map<String, Object>> ingestDocument(MultipartFile file, String metadata) throws IOException {
        try {
            log.info("Starting document ingestion for file: {}", file.getOriginalFilename());
            
            // Validate file
            validateFile(file);
            
            // Extract text content
            String textContent = extractTextFromFile(file);
            log.debug("Extracted {} characters from file", textContent.length());
            
            // Process the content
            String title = file.getOriginalFilename();
            String enrichedMetadata = buildMetadata(metadata, title, file.getSize());
            
            return processDocumentContent(textContent, title, enrichedMetadata);
            
        } catch (Exception e) {
            log.error("Failed to ingest document {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            throw new AIServiceException("Document ingestion failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ApiResponse<Map<String, Object>> ingestTextContent(String content, String title, String metadata) {
        try {
            log.info("Starting text content ingestion with {} characters", content.length());
            
            String documentTitle = StringUtils.hasText(title) ? title : "Text Document";
            String enrichedMetadata = buildMetadata(metadata, documentTitle, content.length());
            
            return processDocumentContent(content, documentTitle, enrichedMetadata);
            
        } catch (Exception e) {
            log.error("Failed to ingest text content: {}", e.getMessage(), e);
            throw new AIServiceException("Text content ingestion failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ApiResponse<List<Map<String, Object>>> listDocuments() {
        try {
            log.info("Retrieving list of all documents");
            
            List<Document> documents = vectorStoreRepository.findAllDocuments();
            
            // Group documents by source/filename and aggregate metadata
            Map<String, List<Document>> documentGroups = documents.stream()
                    .collect(Collectors.groupingBy(doc -> 
                        doc.getMetadata().getOrDefault("filename", "unknown").toString()));
            
            List<Map<String, Object>> documentList = new ArrayList<>();
            for (Map.Entry<String, List<Document>> entry : documentGroups.entrySet()) {
                String filename = entry.getKey();
                List<Document> chunks = entry.getValue();
                
                Map<String, Object> documentInfo = new HashMap<>();
                documentInfo.put("filename", filename);
                documentInfo.put("chunkCount", chunks.size());
                
                // Get metadata from first chunk
                if (!chunks.isEmpty()) {
                    Document firstChunk = chunks.get(0);
                    documentInfo.put("metadata", firstChunk.getMetadata());
                    documentInfo.put("totalCharacters", chunks.stream()
                            .mapToInt(doc -> doc.getText().length()).sum());
                }
                
                documentList.add(documentInfo);
            }
            
            log.info("Retrieved information for {} documents", documentList.size());
            return ApiResponse.success(documentList, "Document list retrieved successfully");
            
        } catch (Exception e) {
            log.error("Failed to list documents: {}", e.getMessage(), e);
            throw new VectorStoreException("Failed to retrieve document list: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ApiResponse<Map<String, Object>> deleteDocuments(String documentIds) {
        try {
            log.info("Deleting documents with IDs: {}", documentIds);
            
            List<String> idList = Arrays.stream(documentIds.split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList());
            
            if (idList.isEmpty()) {
                throw new IllegalArgumentException("No valid document IDs provided");
            }
            
            vectorStoreRepository.deleteDocuments(idList);
            
            Map<String, Object> result = new HashMap<>();
            result.put("deletedIds", idList);
            result.put("count", idList.size());
            result.put("timestamp", LocalDateTime.now());
            
            log.info("Successfully deleted {} documents", idList.size());
            return ApiResponse.success(result, "Documents deleted successfully");
            
        } catch (Exception e) {
            log.error("Failed to delete documents: {}", e.getMessage(), e);
            throw new VectorStoreException("Failed to delete documents: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ApiResponse<Map<String, Object>> getDocumentStatistics() {
        try {
            log.info("Retrieving document store statistics");
            
            List<Document> allDocuments = vectorStoreRepository.findAllDocuments();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalDocuments", allDocuments.size());
            stats.put("totalCharacters", allDocuments.stream()
                    .mapToInt(doc -> doc.getText().length()).sum());
            
            // Count unique sources/files
            Set<String> uniqueSources = allDocuments.stream()
                    .map(doc -> doc.getMetadata().getOrDefault("filename", "unknown").toString())
                    .collect(Collectors.toSet());
            stats.put("uniqueFiles", uniqueSources.size());
            
            // Calculate average chunk size
            OptionalDouble avgChunkSize = allDocuments.stream()
                    .mapToInt(doc -> doc.getText().length())
                    .average();
            stats.put("averageChunkSize", avgChunkSize.orElse(0.0));
            
            // File type distribution
            Map<String, Long> fileTypes = allDocuments.stream()
                    .collect(Collectors.groupingBy(
                        doc -> getFileExtension(doc.getMetadata().getOrDefault("filename", "unknown").toString()),
                        Collectors.counting()));
            stats.put("fileTypes", fileTypes);
            
            stats.put("timestamp", LocalDateTime.now());
            stats.put("hasDocuments", vectorStoreRepository.hasDocuments());
            
            log.info("Retrieved statistics for {} documents from {} unique files", 
                    allDocuments.size(), uniqueSources.size());
            return ApiResponse.success(stats, "Document statistics retrieved successfully");
            
        } catch (Exception e) {
            log.error("Failed to retrieve document statistics: {}", e.getMessage(), e);
            throw new VectorStoreException("Failed to retrieve statistics: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean isSupportedFileFormat(String filename) {
        if (!StringUtils.hasText(filename)) {
            return false;
        }
        
        String extension = getFileExtension(filename).toLowerCase();
        return SUPPORTED_EXTENSIONS.contains(extension);
    }
    
    @Override
    public String extractTextFromFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // For now, handle text files directly
        // In a production system, you might want to add support for PDF, DOC, etc.
        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }
    
    /**
     * Processes document content by chunking, embedding, and storing in vector database.
     * 
     * @param content the text content to process
     * @param title the document title
     * @param metadata the document metadata
     * @return processing results
     */
    private ApiResponse<Map<String, Object>> processDocumentContent(String content, String title, String metadata) {
        long startTime = System.currentTimeMillis();
        
        // Create document with metadata
        Map<String, Object> documentMetadata = new HashMap<>();
        documentMetadata.put("filename", title);
        documentMetadata.put("source", metadata);
        documentMetadata.put("ingestionTime", LocalDateTime.now().toString());
        documentMetadata.put("originalLength", content.length());
        
        Document document = new Document(content, documentMetadata);
        
        // Split document into chunks
        TokenTextSplitter splitter = new TokenTextSplitter(
                DEFAULT_CHUNK_SIZE,
                DEFAULT_OVERLAP_SIZE,
                5,
                10000,
                true
        );
        List<Document> chunks = splitter.apply(List.of(document));
        
        // Store chunks in vector database
        vectorStoreRepository.storeDocuments(chunks);
        
        long processingTime = System.currentTimeMillis() - startTime;
        
        // Build response
        Map<String, Object> result = new HashMap<>();
        result.put("filename", title);
        result.put("originalLength", content.length());
        result.put("chunkCount", chunks.size());
        result.put("processingTimeMs", processingTime);
        result.put("metadata", documentMetadata);
        result.put("timestamp", LocalDateTime.now());
        
        log.info("Successfully processed document '{}' into {} chunks in {}ms", 
                title, chunks.size(), processingTime);
        
        return ApiResponse.success(result, "Document processed and stored successfully");
    }
    
    /**
     * Validates uploaded file for size and format constraints.
     * 
     * @param file the file to validate
     * @throws IllegalArgumentException if file validation fails
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " + 
                    (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }
        
        if (!isSupportedFileFormat(file.getOriginalFilename())) {
            throw new IllegalArgumentException("Unsupported file format. Supported formats: " + 
                    String.join(", ", SUPPORTED_EXTENSIONS));
        }
    }
    
    /**
     * Builds enriched metadata string combining user metadata with system information.
     * 
     * @param userMetadata user-provided metadata
     * @param title document title
     * @param size document size
     * @return enriched metadata string
     */
    private String buildMetadata(String userMetadata, String title, long size) {
        StringBuilder metadata = new StringBuilder();
        metadata.append("title=").append(title);
        metadata.append(", size=").append(size);
        metadata.append(", ingestionTime=").append(LocalDateTime.now());
        
        if (StringUtils.hasText(userMetadata)) {
            metadata.append(", userMetadata=").append(userMetadata);
        }
        
        return metadata.toString();
    }
    
    /**
     * Extracts file extension from filename.
     * 
     * @param filename the filename
     * @return file extension or empty string if none found
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1 || lastDot == filename.length() - 1) {
            return "";
        }
        
        return filename.substring(lastDot + 1).toLowerCase();
    }
}

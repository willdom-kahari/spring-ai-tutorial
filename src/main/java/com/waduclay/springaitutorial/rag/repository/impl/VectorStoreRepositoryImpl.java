package com.waduclay.springaitutorial.rag.repository.impl;

import com.waduclay.springaitutorial.rag.repository.VectorStoreRepository;
import com.waduclay.springaitutorial.shared.exception.VectorStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implementation of VectorStoreRepository for vector database operations.
 * This repository implementation encapsulates all vector store operations,
 * providing a clean data access layer that isolates business logic from
 * vector database specifics. It handles document storage, retrieval, and
 * similarity search operations with proper error handling and logging.
 *
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Repository
public class VectorStoreRepositoryImpl implements VectorStoreRepository {

    private static final Logger log = LoggerFactory.getLogger(VectorStoreRepositoryImpl.class);

    private final VectorStore vectorStore;

    /**
     * Constructs a new VectorStoreRepositoryImpl with the provided VectorStore.
     *
     * @param vectorStore the VectorStore instance for database operations
     */
    public VectorStoreRepositoryImpl(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void storeDocuments(List<Document> documents) {
        try {
            log.info("Storing {} documents in vector store", documents.size());
            vectorStore.add(documents);
            log.info("Successfully stored {} documents", documents.size());
        } catch (Exception e) {
            log.error("Failed to store documents in vector store: {}", e.getMessage(), e);
            throw new VectorStoreException("Failed to store documents: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Document> findSimilarDocuments(SearchRequest searchRequest) {
        try {
            log.debug("Performing similarity search with query: {}", searchRequest.getQuery());
            List<Document> results = vectorStore.similaritySearch(searchRequest);
            log.debug("Found {} similar documents", results.size());
            return results;
        } catch (Exception e) {
            log.error("Failed to perform similarity search: {}", e.getMessage(), e);
            throw new VectorStoreException("Failed to search vector store: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteDocuments(List<String> documentIds) {
        try {
            log.info("Deleting {} documents from vector store", documentIds.size());
            vectorStore.delete(documentIds);
            log.info("Successfully deleted {} documents", documentIds.size());
        } catch (Exception e) {
            log.error("Failed to delete documents from vector store: {}", e.getMessage(), e);
            throw new VectorStoreException("Failed to delete documents: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Document> findAllDocuments() {
        try {
            log.debug("Retrieving all documents from vector store");
            // Note: This is a conceptual method as VectorStore doesn't directly support findAll
            // In a real implementation, you might need to use a different approach or 
            // maintain a separate index of document IDs
            SearchRequest searchRequest = SearchRequest.builder()
                    .query("*") // Wildcard query to get all documents
                    .topK(Integer.MAX_VALUE)
                    .build();
            List<Document> results = vectorStore.similaritySearch(searchRequest);
            log.debug("Retrieved {} documents", results.size());
            return results;
        } catch (Exception e) {
            log.error("Failed to retrieve all documents: {}", e.getMessage(), e);
            throw new VectorStoreException("Failed to retrieve documents: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hasDocuments() {
        try {
            log.debug("Checking if vector store has documents");
            SearchRequest searchRequest = SearchRequest.builder()
                    .query("test")
                    .topK(1)
                    .build();
            List<Document> results = vectorStore.similaritySearch(searchRequest);
            boolean hasDocuments = !results.isEmpty();
            log.debug("Vector store has documents: {}", hasDocuments);
            return hasDocuments;
        } catch (Exception e) {
            log.warn("Failed to check if vector store has documents: {}", e.getMessage());
            // Return false as a safe default if we can't determine the state
            return false;
        }
    }
}

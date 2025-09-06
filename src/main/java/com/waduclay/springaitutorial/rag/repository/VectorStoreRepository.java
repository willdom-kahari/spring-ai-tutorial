package com.waduclay.springaitutorial.rag.repository;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;

import java.util.List;

/**
 * Repository interface for vector store operations.
 * This interface provides a clean abstraction layer for all vector database operations,
 * following the repository pattern to separate data access logic from business logic.
 * It encapsulates vector store operations such as document storage, similarity search,
 * and vector database management.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
public interface VectorStoreRepository {
    
    /**
     * Stores documents in the vector store.
     * This method adds the provided documents to the vector database,
     * generating embeddings if necessary and persisting them for future similarity searches.
     * 
     * @param documents the list of documents to store in the vector database
     * @throws com.waduclay.springaitutorial.exception.VectorStoreException if storage operation fails
     */
    void storeDocuments(List<Document> documents);
    
    /**
     * Performs similarity search in the vector store.
     * This method searches for documents similar to the query in the provided search request,
     * returning the most relevant documents based on vector similarity calculations.
     * 
     * @param searchRequest the search request containing query and search parameters
     * @return list of documents similar to the query, ordered by similarity score
     * @throws com.waduclay.springaitutorial.exception.VectorStoreException if search operation fails
     */
    List<Document> findSimilarDocuments(SearchRequest searchRequest);
    
    /**
     * Deletes documents from the vector store.
     * This method removes the specified documents from the vector database,
     * cleaning up storage space and removing outdated or unwanted content.
     * 
     * @param documentIds the list of document IDs to delete from the vector store
     * @throws com.waduclay.springaitutorial.exception.VectorStoreException if deletion operation fails
     */
    void deleteDocuments(List<String> documentIds);
    
    /**
     * Retrieves all documents from the vector store.
     * This method returns all documents currently stored in the vector database,
     * useful for administrative operations and data export.
     * 
     * @return list of all documents in the vector store
     * @throws com.waduclay.springaitutorial.exception.VectorStoreException if retrieval operation fails
     */
    List<Document> findAllDocuments();
    
    /**
     * Checks if the vector store contains any documents.
     * This method provides a quick way to determine if the vector database
     * has been initialized and contains data.
     * 
     * @return true if the vector store contains documents, false otherwise
     * @throws com.waduclay.springaitutorial.exception.VectorStoreException if check operation fails
     */
    boolean hasDocuments();

}

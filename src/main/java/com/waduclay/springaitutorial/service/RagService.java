package com.waduclay.springaitutorial.service;

import com.waduclay.springaitutorial.dto.ApiResponse;

import java.util.List;
import java.util.Map;

/**
 * Service interface for Retrieval-Augmented Generation (RAG) operations.
 * This interface defines the contract for RAG functionality, providing a clean
 * separation between controller logic and business logic for contextual AI responses
 * that combine vector similarity search with AI chat completion.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
public interface RagService {
    
    /**
     * Generates a contextually-aware AI response using RAG methodology.
     * This method performs similarity search in the vector store to find relevant documents,
     * then uses those documents as context to generate an informed AI response.
     * 
     * The process involves:
     * 1. Performing similarity search on the vector store with the input message
     * 2. Retrieving the top-k most similar documents
     * 3. Using the retrieved documents as context in a prompt template
     * 4. Generating an AI response based on the contextualized prompt
     * 
     * @param message the input question or message to search for and respond to
     * @return ApiResponse containing the RAG-generated response with document context
     */
    ApiResponse<String> generateRagResponse(String message);

}

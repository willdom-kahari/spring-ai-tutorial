package com.waduclay.springaitutorial.rag.service.impl;

import com.waduclay.springaitutorial.shared.dto.ApiResponse;
import com.waduclay.springaitutorial.shared.exception.AIServiceException;
import com.waduclay.springaitutorial.shared.exception.VectorStoreException;
import com.waduclay.springaitutorial.rag.repository.VectorStoreRepository;
import com.waduclay.springaitutorial.rag.service.RagService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Implementation of RagService for Retrieval-Augmented Generation operations.
 * This service encapsulates the business logic for RAG functionality, combining
 * vector similarity search with AI chat completion to provide contextually relevant
 * responses based on stored documents. It handles the complete RAG pipeline from
 * document retrieval to response generation.
 *
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Service
public class RagServiceImpl implements RagService {

    /**
     * Default number of top similar documents to retrieve from vector store
     */
    private static final int DEFAULT_TOP_K = 2;

    private final ChatClient chatClient;
    private final VectorStoreRepository vectorStoreRepository;

    @Value("classpath:prompts/rag-prompt-template.st")
    private Resource ragTemplate;

    /**
     * Constructs a new RagServiceImpl with the provided dependencies.
     *
     * @param chatClient            the ChatClient for AI response generation
     * @param vectorStoreRepository the VectorStoreRepository for similarity search operations
     */
    public RagServiceImpl(ChatClient chatClient, VectorStoreRepository vectorStoreRepository) {
        this.chatClient = chatClient;
        this.vectorStoreRepository = vectorStoreRepository;
    }

    @Override
    public ApiResponse<String> generateRagResponse(String message) {
        try {
            List<Document> similarDocs = vectorStoreRepository.findSimilarDocuments(
                    SearchRequest.builder()
                            .query(message)
                            .topK(DEFAULT_TOP_K)
                            .build()
            );
            List<String> contentList = similarDocs.stream().map(Document::getText).toList();
            Prompt prompt = PromptTemplate.builder()
                    .resource(ragTemplate)
                    .variables(Map.of("input", message, "documents", String.join("\n", contentList)))
                    .build()
                    .create();
            String response = chatClient.prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();
            return ApiResponse.success(response, "RAG response generated successfully");
        } catch (Exception e) {
            if (e.getMessage().contains("vector") || e.getMessage().contains("search")) {
                throw new VectorStoreException("Failed to search vector store: " + e.getMessage(), e);
            } else {
                throw new AIServiceException("Failed to generate RAG response: " + e.getMessage(), e);
            }
        }
    }

}

package com.waduclay.springaitutorial.rag.controller;


import com.waduclay.springaitutorial.rag.service.RagService;
import com.waduclay.springaitutorial.shared.dto.ApiResponse;
import com.waduclay.springaitutorial.shared.dto.QueryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for Retrieval-Augmented Generation (RAG) operations.
 * This controller implements RAG functionality by combining vector similarity search
 * with AI chat completion to provide contextually relevant responses based on stored documents.
 * It searches for similar documents in the vector store and uses them as context for AI responses.
 *
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@RestController
@RequestMapping("/api/v1/rag")
@Validated
@Tag(name = "RAG Operations", description = "Retrieval-Augmented Generation endpoints for contextual AI responses")
public class RagController {
    private final RagService ragService;

    /**
     * Constructs a new RagController with the provided RagService.
     *
     * @param ragService the RagService used for RAG operations
     */
    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    /**
     * Generates a contextually-aware AI response using RAG methodology.
     * This endpoint performs similarity search in the vector store to find relevant documents,
     * then uses those documents as context to generate an informed AI response.
     * <p>
     * The process involves:
     * 1. Performing similarity search on the vector store with the input query
     * 2. Retrieving the top-k most similar documents
     * 3. Using the retrieved documents as context in a prompt template
     * 4. Generating an AI response based on the contextualized prompt
     *
     * @param request the request containing the input query for RAG processing
     * @return ApiResponse containing the RAG-generated response with document context
     */
    @Operation(
            summary = "Generate RAG Response",
            description = "Generates a contextually-aware AI response using Retrieval-Augmented Generation (RAG) methodology based on stored documents."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully generated RAG response"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input parameters"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "503",
                    description = "RAG service unavailable"
            )
    })
    @PostMapping("/query")
    public ApiResponse<String> generate(
            @Parameter(description = "Request containing the query for RAG processing")
            @Valid @RequestBody QueryRequest request) {
        return ragService.generateRagResponse(request.getQuery());
    }

    /**
     * Performs document similarity search without AI generation.
     * This endpoint searches the vector store for documents similar to the input query
     * and returns the matching documents with their content, metadata, and similarity scores
     * without generating an AI response. This is useful for applications that need to find
     * relevant documents but handle the AI generation separately or not at all.
     *
     * @param request the request containing the query and optional topK for document search
     * @return ApiResponse containing a list of similar documents with content and similarity scores
     */
    @Operation(
            summary = "Search Similar Documents",
            description = "Performs document similarity search without AI generation. Returns similar documents with content, metadata, and similarity scores."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully found similar documents"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input parameters"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "503",
                    description = "Vector store service unavailable"
            )
    })
    @PostMapping("/search")
    public ApiResponse<List<Map<String, Object>>> search(
            @Parameter(description = "Request containing query and optional topK for document search")
            @Valid @RequestBody QueryRequest request) {
        return ragService.searchDocuments(request.getQuery(), request.getTopK());
    }
}

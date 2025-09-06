package com.waduclay.springaitutorial.rag.controller;


import com.waduclay.springaitutorial.shared.dto.ApiResponse;
import com.waduclay.springaitutorial.rag.service.RagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
     * 
     * The process involves:
     * 1. Performing similarity search on the vector store with the input message
     * 2. Retrieving the top-k most similar documents
     * 3. Using the retrieved documents as context in a prompt template
     * 4. Generating an AI response based on the contextualized prompt
     * 
     * @param message the input question or message to search for and respond to (1-500 characters, cannot be blank)
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
    @GetMapping("/faq")
    public ApiResponse<String> generate(
            @Parameter(description = "Input question or message for RAG processing", example = "What services do you offer?")
            @RequestParam(value = "message", defaultValue = "What services do you offer?")
                          @NotBlank(message = "Message cannot be blank")
                          @Size(min = 1, max = 500, message = "Message must be between 1 and 500 characters")
                          String message){
        return ragService.generateRagResponse(message);
    }
}

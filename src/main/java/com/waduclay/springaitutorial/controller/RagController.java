package com.waduclay.springaitutorial.controller;


import com.waduclay.springaitutorial.dto.ApiResponse;
import com.waduclay.springaitutorial.service.RagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REST controller for Retrieval-Augmented Generation (RAG) operations.
 * This controller implements RAG functionality by combining vector similarity search
 * with AI chat completion to provide contextually relevant responses based on stored documents.
 * It searches for similar documents in the vector store and uses them as context for AI responses.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@RestController
@Validated
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
    @GetMapping("/faq")
    public ApiResponse<String> generate(@RequestParam(value = "message", defaultValue = "Tell me a Dad joke") 
                          @NotBlank(message = "Message cannot be blank")
                          @Size(min = 1, max = 500, message = "Message must be between 1 and 500 characters")
                          String message){
        return ragService.generateRagResponse(message);
    }
}

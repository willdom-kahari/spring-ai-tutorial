package com.waduclay.springaitutorial.chat.controller;


import com.waduclay.springaitutorial.chat.service.ChatService;
import com.waduclay.springaitutorial.shared.dto.ApiResponse;
import com.waduclay.springaitutorial.shared.dto.PromptRequest;
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

/**
 * REST controller for basic AI chat interactions.
 * This controller provides endpoints for generating AI responses using the ChatService.
 * All business logic has been extracted to the service layer for better separation of concerns.
 * All responses are wrapped in a standardized ApiResponse format for consistency.
 *
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@RestController
@RequestMapping("/api/v1/chat")
@Validated
@Tag(name = "AI Chat", description = "Basic AI chat interactions and response generation")
public class ChatGenerationController {
    private final ChatService chatService;

    /**
     * Constructs a new ChatGenerationController with the provided ChatService.
     *
     * @param chatService the ChatService used for AI chat operations
     */
    public ChatGenerationController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Generates an AI response based on the provided prompt.
     * This endpoint accepts a user prompt in the request body and returns an AI-generated response,
     * typically used for dad jokes or general chat interactions.
     *
     * @param request the request containing the input prompt for the AI to respond to
     * @return ApiResponse containing the AI-generated response wrapped in a success format
     */
    @Operation(
            summary = "Generate AI Response",
            description = "Generates an AI response based on the provided prompt. The AI will typically respond with dad jokes or general chat responses."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully generated AI response"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input parameters"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "503",
                    description = "AI service unavailable"
            )
    })
    @PostMapping("/generate")
    public ApiResponse<String> generate(
            @Parameter(description = "Request containing the prompt for AI generation")
            @Valid @RequestBody PromptRequest request) {
        return chatService.generateResponse(request.getPrompt());
    }
}

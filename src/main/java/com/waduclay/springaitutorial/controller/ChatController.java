package com.waduclay.springaitutorial.controller;


import com.waduclay.springaitutorial.dto.ApiResponse;
import com.waduclay.springaitutorial.service.ChatService;
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
public class ChatController {
    private final ChatService chatService;

    /**
     * Constructs a new AiController with the provided ChatService.
     * 
     * @param chatService the ChatService used for AI chat operations
     */
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Generates an AI response based on the provided message.
     * This endpoint accepts a user message and returns an AI-generated response,
     * typically used for dad jokes or general chat interactions.
     * 
     * @param message the input message for the AI to respond to (1-500 characters, cannot be blank)
     * @return ApiResponse containing the AI-generated response wrapped in a success format
     */
    @Operation(
        summary = "Generate AI Response",
        description = "Generates an AI response based on the provided message. The AI will typically respond with dad jokes or general chat responses."
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
    @GetMapping("/basic")
    public ApiResponse<String> generate(
            @Parameter(description = "Input message for AI to respond to", example = "Tell me a Dad joke")
            @RequestParam(value = "message", defaultValue = "Tell me a Dad joke") 
            @NotBlank(message = "Message cannot be blank")
            @Size(min = 1, max = 500, message = "Message must be between 1 and 500 characters")
            String message){
        return chatService.generateResponse(message);
    }
}

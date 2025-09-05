package com.waduclay.springaitutorial.intro;


import com.waduclay.springaitutorial.dto.ApiResponse;
import com.waduclay.springaitutorial.exception.AIServiceException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REST controller for basic AI chat interactions.
 * This controller provides endpoints for generating AI responses using OpenAI's ChatGPT.
 * All responses are wrapped in a standardized ApiResponse format for consistency.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@RestController
@Validated
public class AiController {
    private final ChatClient chatClient;

    /**
     * Constructs a new AiController with the provided ChatClient builder.
     * 
     * @param chatClient the ChatClient builder used to create the chat client instance
     */
    public AiController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    /**
     * Generates an AI response based on the provided message.
     * This endpoint accepts a user message and returns an AI-generated response,
     * typically used for dad jokes or general chat interactions.
     * 
     * @param message the input message for the AI to respond to (1-500 characters, cannot be blank)
     * @return ApiResponse containing the AI-generated response wrapped in a success format
     * @throws AIServiceException if the AI service fails to generate a response
     */
    @GetMapping("/dad-jokes")
    public ApiResponse<String> generate(@RequestParam(value = "message", defaultValue = "Tell me a Dad joke") 
                          @NotBlank(message = "Message cannot be blank")
                          @Size(min = 1, max = 500, message = "Message must be between 1 and 500 characters")
                          String message){
        try {
            String response = chatClient.prompt(message)
                    .call()
                    .content();
            return ApiResponse.success(response, "AI response generated successfully");
        } catch (Exception e) {
            throw new AIServiceException("Failed to generate response: " + e.getMessage(), e);
        }
    }
}

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
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@RestController
@Validated
public class AiController {
    private final ChatClient chatClient;

    public AiController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

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

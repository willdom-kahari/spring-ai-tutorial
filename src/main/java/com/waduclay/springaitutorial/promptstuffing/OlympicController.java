package com.waduclay.springaitutorial.promptstuffing;

import com.waduclay.springaitutorial.dto.ApiResponse;
import com.waduclay.springaitutorial.exception.AIServiceException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * REST controller demonstrating prompt stuffing techniques for contextual AI responses.
 * This controller showcases the technique of "prompt stuffing" - dynamically injecting 
 * relevant context documents into prompts to improve AI response accuracy and relevance.
 * It demonstrates conditional context loading based on user preferences.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@RestController
@RequestMapping("/olympics")
@Validated
public class OlympicController {
    private final ChatClient chatClient;
    @Value("classpath:prompts/olympic-sports.st")
    private Resource promptTemplate;
    @Value("classpath:docs/olympic-sports.txt")
    private Resource olympicSports;
    
    /**
     * Constructs a new OlympicController with the provided ChatClient builder.
     * 
     * @param chatClient the ChatClient builder used to create the chat client instance
     */
    public OlympicController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    /**
     * Generates responses about 2024 Olympic sports with optional context stuffing.
     * This endpoint demonstrates prompt stuffing by conditionally injecting Olympic sports
     * documentation into the prompt context. When stuffit=true, the AI has access to
     * detailed Olympic sports information; when false, it relies only on its training data.
     * 
     * @param message the question about Olympic sports (1-500 characters, cannot be blank)
     * @param stuffit whether to inject Olympic sports context document into the prompt
     * @return ApiResponse containing Olympic sports information with or without context stuffing
     * @throws AIServiceException if the AI service fails to generate a response
     * @throws IOException if there's an error reading the Olympic sports document
     */
    @GetMapping("/2024")
    public ApiResponse<String> get2024OlympicSports(
            @RequestParam(value = "message", defaultValue = "What sports are being includen in the 2024 summer olympics?") 
            @NotBlank(message = "Message cannot be blank")
            @Size(min = 1, max = 500, message = "Message must be between 1 and 500 characters")
            String message,

            @RequestParam(value = "stuffit", defaultValue = "false") boolean stuffit
    ) throws IOException {
        try {
            PromptTemplate template = PromptTemplate.builder()
                    .resource(promptTemplate)
                    .variables(Map.of("question", message, "context",""))
                    .build();

            if (stuffit) {
                template.add("context", olympicSports.getContentAsString(Charset.defaultCharset()) );
            }
            Prompt prompt = template.create();
            String response = chatClient.prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();
            return ApiResponse.success(response, "Olympic sports information generated successfully");
        } catch (Exception e) {
            throw new AIServiceException("Failed to generate Olympic sports information: " + e.getMessage(), e);
        }
    }
}

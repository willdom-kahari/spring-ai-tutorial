package com.waduclay.springaitutorial.prompting;


import com.waduclay.springaitutorial.dto.ApiResponse;
import com.waduclay.springaitutorial.exception.AIServiceException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@RestController
@Validated
public class SimplePromptController {
    private final ChatClient chatClient;
    @Value("classpath:prompts/youtube.st")
    private Resource youTubePromptTemplate;

    public SimplePromptController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/simple-prompt")
    public ApiResponse<String> simplePrompt() {
        try {
            Prompt prompt = new Prompt(new UserMessage("Tell me a dad joke"));
            String response = chatClient.prompt(prompt)
                    .call()
                    .content();
            return ApiResponse.success(response, "Simple prompt response generated successfully");
        } catch (Exception e) {
            throw new AIServiceException("Failed to generate simple prompt response: " + e.getMessage(), e);
        }
    }

    @GetMapping("/yt")
    public ApiResponse<String> simplePromptTemplate(@RequestParam(value = "genre", defaultValue = "tech") 
                                      @NotBlank(message = "Genre cannot be blank")
                                      @Size(min = 1, max = 50, message = "Genre must be between 1 and 50 characters")
                                      String genre) {
        try {
            String message = """
                    List 10 of the most popular Youtubers in {genre} along with their current subscriber counts.
                    If you don't know the answer, just say "I don't know".
                    """;
            PromptTemplate template = PromptTemplate.builder()
                    .template(message)
                    .variables(Map.of("genre", genre))
                    .build();
            Prompt prompt = template.create();
            String response = chatClient.prompt(prompt)
                    .call()
                    .content();
            return ApiResponse.success(response, "YouTube list generated successfully");
        } catch (Exception e) {
            throw new AIServiceException("Failed to generate YouTube list: " + e.getMessage(), e);
        }
    }

    @GetMapping("/yt-ex")
    public ApiResponse<String> simpleExtPromptTemplate(@RequestParam(value = "genre", defaultValue = "tech") 
                                         @NotBlank(message = "Genre cannot be blank")
                                         @Size(min = 1, max = 50, message = "Genre must be between 1 and 50 characters")
                                         String genre) {
        try {
            PromptTemplate template = PromptTemplate.builder()
                    .resource(youTubePromptTemplate)
                    .variables(Map.of("genre", genre))
                    .build();
            Prompt prompt = template.create();
            String response = chatClient.prompt(prompt)
                    .call()
                    .content();
            return ApiResponse.success(response, "YouTube extended list generated successfully");
        } catch (Exception e) {
            throw new AIServiceException("Failed to generate YouTube extended list: " + e.getMessage(), e);
        }
    }

    @GetMapping("/jokes")
    public ApiResponse<String> dadJoke() {
        try {
            SystemMessage systemMessage = new SystemMessage("You are a world class comedian. Your task is to tell dad jokes. If someone asks you about any other jokes, tell them you only ");
            UserMessage us = new UserMessage("Tell me a serious joke about the universe");
            Prompt prompt = new Prompt(systemMessage, us);
            String response = chatClient.prompt(prompt)
                    .call()
                    .content();
            return ApiResponse.success(response, "Dad joke generated successfully");
        } catch (Exception e) {
            throw new AIServiceException("Failed to generate dad joke: " + e.getMessage(), e);
        }
    }
}

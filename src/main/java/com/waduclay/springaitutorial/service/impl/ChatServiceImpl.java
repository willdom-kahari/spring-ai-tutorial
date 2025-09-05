package com.waduclay.springaitutorial.service.impl;

import com.waduclay.springaitutorial.dto.ApiResponse;
import com.waduclay.springaitutorial.exception.AIServiceException;
import com.waduclay.springaitutorial.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementation of ChatService for AI chat operations.
 * This service encapsulates the business logic for AI chat functionality,
 * providing a clean separation between controller and business logic layers.
 * It handles various types of AI interactions including simple prompts,
 * template-based prompts, external templates, and system message interactions.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Service
public class ChatServiceImpl implements ChatService {
    
    private final ChatClient chatClient;
    
    @Value("classpath:prompts/youtube.st")
    private Resource youTubePromptTemplate;
    
    /**
     * Constructs a new ChatServiceImpl with the provided ChatClient builder.
     * 
     * @param chatClient the ChatClient builder used to create the chat client instance
     */
    public ChatServiceImpl(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }
    
    @Override
    public ApiResponse<String> generateResponse(String message) {
        try {
            String response = chatClient.prompt(message)
                    .call()
                    .content();
            return ApiResponse.success(response, "AI response generated successfully");
        } catch (Exception e) {
            throw new AIServiceException("Failed to generate response: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ApiResponse<String> generateSimplePrompt() {
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
    
    @Override
    public ApiResponse<String> generatePromptTemplate(String genre) {
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
    
    @Override
    public ApiResponse<String> generateExternalPromptTemplate(String genre) {
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
    
    @Override
    public ApiResponse<String> generateWithSystemMessage() {
        try {
            SystemMessage systemMessage = new SystemMessage("You are a world class comedian. Your task is to tell dad jokes. If someone asks you about any other jokes, tell them you only ");
            UserMessage userMessage = new UserMessage("Tell me a serious joke about the universe");
            Prompt prompt = new Prompt(systemMessage, userMessage);
            String response = chatClient.prompt(prompt)
                    .call()
                    .content();
            return ApiResponse.success(response, "Dad joke generated successfully");
        } catch (Exception e) {
            throw new AIServiceException("Failed to generate dad joke: " + e.getMessage(), e);
        }
    }
}

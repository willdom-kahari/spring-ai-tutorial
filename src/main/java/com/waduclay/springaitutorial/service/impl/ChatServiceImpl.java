package com.waduclay.springaitutorial.service.impl;

import com.waduclay.springaitutorial.dto.ApiResponse;
import com.waduclay.springaitutorial.exception.AIServiceException;
import com.waduclay.springaitutorial.service.ChatService;
import com.waduclay.springaitutorial.security.InputSanitizer;
import com.waduclay.springaitutorial.security.ContentFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
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
    
    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);
    
    private final ChatClient chatClient;
    private final InputSanitizer inputSanitizer;
    private final ContentFilter contentFilter;
    
    @Value("classpath:prompts/youtube.st")
    private Resource youTubePromptTemplate;

    @Value("classpath:prompts/olympic-sports.st")
    private Resource olympicPromptTemplate;

    @Value("classpath:docs/olympic-sports.txt")
    private Resource olympicSports;
    /**
     * Constructs a new ChatServiceImpl with the provided ChatClient builder and security components.
     * 
     * @param chatClient the ChatClient builder used to create the chat client instance
     * @param inputSanitizer the InputSanitizer for input validation and sanitization
     * @param contentFilter the ContentFilter for content filtering
     */
    public ChatServiceImpl(ChatClient.Builder chatClient, InputSanitizer inputSanitizer, ContentFilter contentFilter) {
        this.chatClient = chatClient.build();
        this.inputSanitizer = inputSanitizer;
        this.contentFilter = contentFilter;
    }
    
    @Override
    public ApiResponse<String> generateResponse(String message) {
        logger.info("Generating AI response for message with length: {}", message != null ? message.length() : 0);
        
        // Comprehensive input security validation and sanitization
        try {
            String sanitizedMessage = inputSanitizer.sanitizeInput(message);
            logger.debug("Input sanitized successfully");
            
            // Content filtering for inappropriate requests
            ContentFilter.FilterResult filterResult = contentFilter.filterContent(sanitizedMessage);
            if (filterResult.isBlocked()) {
                logger.warn("Content blocked due to: {}", filterResult.getReason());
                throw new SecurityException("Request blocked: " + filterResult.getReason());
            }
            
            String processedMessage = filterResult.getFilteredContent();
            logger.debug("Content filtering completed, using processed message");
            
            String response = chatClient.prompt(processedMessage)
                    .call()
                    .content();
            
            // Filter AI response for inappropriate content
            ContentFilter.FilterResult responseFilterResult = contentFilter.filterContent(response);
            String finalResponse = responseFilterResult.getFilteredContent();
            
            logger.info("AI response generated successfully, response length: {}", finalResponse.length());
            return ApiResponse.success(finalResponse, "AI response generated successfully");
            
        } catch (SecurityException e) {
            logger.error("Security validation failed: {}", e.getMessage());
            throw e; // Re-throw security exceptions
        } catch (Exception e) {
            logger.error("Failed to generate AI response: {}", e.getMessage(), e);
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
            SystemMessage systemMessage = new SystemMessage("You are a world class comedian. Your task is to tell dad jokes. If someone asks you about any other jokes, tell them you only tell dad jokes");
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

    @Override
    public ApiResponse<String> stuffThePrompt(String message, boolean stuffit) {
        try {
            PromptTemplate template = PromptTemplate.builder()
                    .resource(olympicPromptTemplate)
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

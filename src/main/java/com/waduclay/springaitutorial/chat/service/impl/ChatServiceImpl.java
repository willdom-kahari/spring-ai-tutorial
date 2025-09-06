package com.waduclay.springaitutorial.chat.service.impl;

import com.waduclay.springaitutorial.chat.service.ChatService;
import com.waduclay.springaitutorial.shared.dto.ApiResponse;
import com.waduclay.springaitutorial.shared.exception.AIServiceException;
import com.waduclay.springaitutorial.shared.security.ContentFilter;
import com.waduclay.springaitutorial.shared.security.InputSanitizer;
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
     * @param chatClient     the ChatClient builder used to create the chat client instance
     * @param inputSanitizer the InputSanitizer for input validation and sanitization
     * @param contentFilter  the ContentFilter for content filtering
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
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();

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
            // PROMPT ENGINEERING TECHNIQUE: Template Variable Substitution
            // This demonstrates dynamic prompt creation by using placeholders ({genre}) 
            // that get replaced with actual values at runtime. This approach allows
            // for flexible, reusable prompt patterns while maintaining consistency.
            String message = """
                    List 10 of the most popular Youtubers in {genre} along with their current subscriber counts.
                    If you don't know the answer, just say "I don't know".
                    """;

            // The PromptTemplate.builder() creates a template that can substitute variables
            // Variables are defined in a Map where key matches the placeholder name
            PromptTemplate template = PromptTemplate.builder()
                    .template(message)
                    .variables(Map.of("genre", genre))
                    .build();

            // The create() method performs the actual variable substitution
            // transforming {genre} into the actual genre value provided by the user
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
            // PROMPT ENGINEERING TECHNIQUE: External Template Files
            // This demonstrates separating prompt content from code by storing templates
            // in external resource files. This approach promotes:
            // 1. Better maintainability - prompts can be updated without code changes
            // 2. Template reusability across different parts of the application
            // 3. Easier collaboration between developers and prompt engineers
            // 4. Version control for prompt modifications
            PromptTemplate template = PromptTemplate.builder()
                    .resource(youTubePromptTemplate) // Loads template from classpath:prompts/youtube.st
                    .variables(Map.of("genre", genre)) // Variables still work with external templates
                    .build();

            // Same substitution process as inline templates, but content comes from file
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
            // PROMPT ENGINEERING TECHNIQUE: System Message Behavior Control
            // System messages are a powerful way to establish the AI's role, personality, 
            // and behavioral constraints. They act as "instructions" that persist throughout
            // the conversation and override the AI's default behavior patterns.
            // Key benefits:
            // 1. Consistent persona across all responses
            // 2. Behavioral boundaries and constraints
            // 3. Domain-specific expertise simulation
            // 4. Reduced need for repetitive instructions in user messages
            SystemMessage systemMessage = new SystemMessage("You are a world class comedian. Your task is to tell dad jokes. If someone asks you about any other jokes, tell them you only tell dad jokes");

            // User message can request anything, but system message will constrain the response
            // Notice how we ask for a "serious joke" but the system message should override this
            UserMessage userMessage = new UserMessage("Tell me a serious joke about the universe");

            // System message is processed first, establishing the behavioral framework
            // for how the AI should interpret and respond to the user message
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
            // PROMPT ENGINEERING TECHNIQUE: Context Injection (Prompt Stuffing)
            // This technique involves injecting relevant context or knowledge directly into
            // the prompt to provide the AI with specific information it might not have in
            // its training data or to ensure factual accuracy for domain-specific queries.
            // Benefits:
            // 1. Provides current/specific information not in AI training data
            // 2. Ensures factual accuracy for specialized domains
            // 3. Reduces hallucination by grounding responses in provided context
            // 4. Allows for dynamic knowledge injection based on user needs
            PromptTemplate template = PromptTemplate.builder()
                    .resource(olympicPromptTemplate) // Template includes {context} placeholder
                    .variables(Map.of("question", message, "context", "")) // Initial empty context
                    .build();

            // Conditional context injection based on user preference
            // When stuffit=true, we inject the full Olympic sports document content
            // This demonstrates how to dynamically control context availability
            if (stuffit) {
                // Load external document content and inject it as context
                // This gives the AI access to specific, factual information about Olympic sports
                template.add("context", olympicSports.getContentAsString(Charset.defaultCharset()));
            }
            // When stuffit=false, the AI relies only on its training knowledge

            // The template now contains either empty context or rich factual content
            // depending on the stuffit flag, demonstrating controlled knowledge injection
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

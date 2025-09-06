package com.waduclay.springaitutorial.shared.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for AI Chat functionality.
 * This configuration centralizes ChatClient creation and resolves multiple ChatModel beans
 * by explicitly selecting the OpenAI chat model. It provides both ChatClient and ChatClient.Builder
 * beans for dependency injection across the application.
 *
 * <p>Usage Examples:</p>
 * <pre>
 * // In a service class:
 * {@code @Autowired}
 * private ChatClient chatClient;
 *
 * // Using the chat client:
 * String response = chatClient.prompt("Tell me a joke")
 *     .call()
 *     .chatResponse()
 *     .getResult()
 *     .getOutput()
 *     .getText();
 * </pre>
 *
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Configuration
public class ChatConfig {

    /**
     * Creates a primary ChatClient bean using the specified OpenAI ChatModel.
     * This method resolves potential conflicts when multiple ChatModel beans exist
     * by explicitly selecting the "openAiChatModel" bean via @Qualifier annotation.
     *
     * <p>The ChatClient is configured as @Primary to be the default choice for
     * dependency injection throughout the application.</p>
     *
     * <p>Usage Example:</p>
     * <pre>
     * {@code @Autowired}
     * private ChatClient chatClient;
     *
     * // Generate simple AI response
     * String joke = chatClient.prompt("Tell me a dad joke")
     *     .call()
     *     .chatResponse()
     *     .getResult()
     *     .getOutput()
     *     .getText();
     * </pre>
     *
     * @param chatModel the OpenAI ChatModel bean to use for building the ChatClient
     * @return configured ChatClient instance ready for AI interactions
     */
    @Bean
    @Primary
    public ChatClient chatClient(@Qualifier("openAiChatModel") ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    /**
     * Creates a primary ChatClient.Builder bean for advanced chat client configuration.
     * This builder allows for more complex ChatClient configurations when needed,
     * such as adding custom advisors, filters, or other advanced settings.
     *
     * <p>The builder is marked as @Primary to serve as the default for dependency injection
     * when ChatClient.Builder is required instead of a pre-built ChatClient.</p>
     *
     * <p>Usage Example:</p>
     * <pre>
     * {@code @Autowired}
     * private ChatClient.Builder chatClientBuilder;
     *
     * // Build custom ChatClient with advisors
     * ChatClient customClient = chatClientBuilder
     *     .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
     *     .build();
     * </pre>
     *
     * @param chatModel the OpenAI ChatModel bean to use for building the ChatClient.Builder
     * @return configured ChatClient.Builder instance for advanced chat client creation
     */
    @Bean
    @Primary
    public ChatClient.Builder chatClientBuilder(@Qualifier("openAiChatModel") ChatModel chatModel) {
        return ChatClient.builder(chatModel);
    }
}

package com.waduclay.springaitutorial.shared.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Centralizes ChatClient creation to resolve multiple ChatModel beans by selecting one.
 */
@Configuration
public class ChatConfig {

    /**
     * Build a primary ChatClient using the ChatModel selected by Spring via @Primary or @Qualifier.
     * If multiple ChatModel beans exist, prefer the one marked @Primary elsewhere or specify via @Qualifier here.
     */
    @Bean
    @Primary
    public ChatClient chatClient(@Qualifier("openAiChatModel") ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    /**
     * Also expose a primary ChatClient.Builder to satisfy controller constructor injection.
     */
    @Bean
    @Primary
    public ChatClient.Builder chatClientBuilder(@Qualifier("openAiChatModel") ChatModel chatModel) {
        return ChatClient.builder(chatModel);
    }
}

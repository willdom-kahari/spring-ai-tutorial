package com.waduclay.springaitutorial.service.impl;

import com.waduclay.springaitutorial.dto.ApiResponse;
import com.waduclay.springaitutorial.exception.AIServiceException;
import com.waduclay.springaitutorial.service.OlympicService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Implementation of OlympicService for Olympic sports prompt stuffing operations.
 * This service encapsulates the business logic for Olympic sports functionality,
 * demonstrating the technique of "prompt stuffing" - dynamically injecting relevant
 * context documents into prompts to improve AI response accuracy and relevance.
 * It shows conditional context loading based on user preferences.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Service
public class OlympicServiceImpl implements OlympicService {
    
    private final ChatClient chatClient;
    
    @Value("classpath:prompts/olympic-sports.st")
    private Resource promptTemplate;
    
    @Value("classpath:docs/olympic-sports.txt")
    private Resource olympicSports;
    
    /**
     * Constructs a new OlympicServiceImpl with the provided ChatClient builder.
     * 
     * @param chatClient the ChatClient builder used to create the chat client instance
     */
    public OlympicServiceImpl(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }
    
    @Override
    public ApiResponse<String> get2024OlympicSports(String message, boolean stuffit) throws IOException {
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

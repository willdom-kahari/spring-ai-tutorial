package com.waduclay.springaitutorial.service.impl;

import com.waduclay.springaitutorial.dto.ApiResponse;
import com.waduclay.springaitutorial.exception.AIServiceException;
import com.waduclay.springaitutorial.service.OutputService;
import com.waduclay.springaitutorial.dto.Author;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Implementation of OutputService for structured output conversion operations.
 * This service encapsulates the business logic for converting AI responses into
 * structured formats including lists, maps, and custom Java objects. It demonstrates
 * various output parsers and converters to transform unstructured AI text into
 * predictable data formats for application integration.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Service
public class OutputServiceImpl implements OutputService {
    
    private final ChatClient chatClient;
    
    /**
     * Constructs a new OutputServiceImpl with the provided ChatClient builder.
     * 
     * @param chatClient the ChatClient builder used to create the chat client instance
     */
    public OutputServiceImpl(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }
    
    @Override
    public ApiResponse<List<String>> generateSongsList(String artist) {
        try {
            String message = """
                    Please give me a list of the top 10 songs by {artist}. If you don't know the answer, just say "I don't know".
                    {format}
                    """;

            ListOutputConverter outputParser = new ListOutputConverter();
            Prompt prompt = PromptTemplate.builder()
                    .template(message)
                    .variables(Map.of("artist", artist, "format", outputParser.getFormat()))
                    .build()
                    .create();

            String content = chatClient.prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();
            List<String> songs = outputParser.convert(content);
            return ApiResponse.success(songs, "Songs list generated successfully");
        } catch (Exception e) {
            throw new AIServiceException("Failed to generate songs list: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ApiResponse<Map<String, Object>> generateAuthorLinks(String author) {
        try {
            String message = """
                    Generate a list of links for the author {author}. Include the author's name as the key and any social network links as the object.
                    {format}
                    """;
            MapOutputConverter outputParser = new MapOutputConverter();

            Prompt prompt = PromptTemplate.builder()
                    .template(message)
                    .variables(Map.of("author", author, "format", outputParser.getFormat()))
                    .build()
                    .create();

            String content = chatClient.prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();
            Map<String, Object> authorLinks = outputParser.convert(content);
            return ApiResponse.success(authorLinks, "Author links generated successfully");
        } catch (Exception e) {
            throw new AIServiceException("Failed to generate author links: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ApiResponse<Author> generateAuthorBooks(String author) {
        try {
            String message = """
                    Generate a list of books written by the author {author}. If you are not positive that the book belongs to this author, please don't include it.
                    {format}
                    """;
            BeanOutputConverter<Author> outputParser = new BeanOutputConverter<>(Author.class);

            Prompt prompt = PromptTemplate.builder()
                    .template(message)
                    .variables(Map.of("author", author, "format", outputParser.getFormat()))
                    .build()
                    .create();

            String content = chatClient.prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();
            Author authorBooks = outputParser.convert(content);
            return ApiResponse.success(authorBooks, "Author books generated successfully");
        } catch (Exception e) {
            throw new AIServiceException("Failed to generate author books: " + e.getMessage(), e);
        }
    }
}

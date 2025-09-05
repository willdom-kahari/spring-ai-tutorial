package com.waduclay.springaitutorial.structuredoutput;


import com.waduclay.springaitutorial.dto.ApiResponse;
import com.waduclay.springaitutorial.exception.AIServiceException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

/**
 * REST controller demonstrating structured output conversion techniques with AI responses.
 * This controller showcases different output parsers and converters to transform unstructured AI text
 * into structured Java objects including lists, maps, and custom beans. This is essential for
 * integrating AI responses into applications that require predictable data formats.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@RestController
@RequestMapping("/output")
@Validated
public class OutputController {
    private final ChatClient chatClient;

    /**
     * Constructs a new OutputController with the provided ChatClient builder.
     * 
     * @param chatClient the ChatClient builder used to create the chat client instance
     */
    public OutputController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    /**
     * Demonstrates converting AI responses to structured list format.
     * This endpoint uses ListOutputConverter to parse AI-generated text into a List of strings,
     * making it easy to work with enumerated data programmatically.
     * 
     * @param artist the artist name to generate a song list for (1-100 characters, cannot be blank)
     * @return ApiResponse containing a list of songs by the specified artist
     * @throws AIServiceException if the AI service fails to generate or convert the response
     */
    @GetMapping("/songs")
    public ApiResponse<List<String>> generate(@RequestParam(value = "artist", defaultValue = "Taylor Swift") 
                                @NotBlank(message = "Artist name cannot be blank")
                                @Size(min = 1, max = 100, message = "Artist name must be between 1 and 100 characters")
                                String artist){
        try {
            String message = """
                    Please give me a list of the top 10 songs by {artist}. If you don't know the answer, just say "I don't know".
                    {format}
                    """;

            ListOutputConverter outputParser = new ListOutputConverter();
            Prompt prompt  = PromptTemplate.builder()
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

    /**
     * Demonstrates converting AI responses to structured map format.
     * This endpoint uses MapOutputConverter to parse AI-generated text into a Map structure,
     * allowing for key-value pair data representation from unstructured AI responses.
     * 
     * @param author the author name to generate social media links for (1-100 characters, cannot be blank)
     * @return ApiResponse containing a map of author information and social network links
     * @throws AIServiceException if the AI service fails to generate or convert the response
     */
    @GetMapping("/{author}")
    public ApiResponse<Map<String, Object>> generateBooks(@PathVariable(value = "author") 
                                           @NotBlank(message = "Author name cannot be blank")
                                           @Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
                                           String author){
        try {
            String message = """
                    Generate a list of links for the author {author}. Include the author's name as the key and any social network links as the object.
                    {format}
                    """;
            MapOutputConverter outputParser = new MapOutputConverter();

            Prompt prompt  = PromptTemplate.builder()
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

    /**
     * Demonstrates converting AI responses to structured custom bean format.
     * This endpoint uses BeanOutputConverter to parse AI-generated text into a custom Java object (Author),
     * showcasing the most advanced form of structured output conversion for complex data models.
     * 
     * @param author the author name to generate book information for (1-100 characters, cannot be blank)
     * @return ApiResponse containing an Author object with structured book information
     * @throws AIServiceException if the AI service fails to generate or convert the response
     */
    @GetMapping("/books")
    public ApiResponse<Author> generateBooksVyAuthor(@RequestParam(value = "author", defaultValue = "Ken Kousen") 
                                       @NotBlank(message = "Author name cannot be blank")
                                       @Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
                                       String author){
        try {
            String message = """
                    Generate a list of books written by the author {author}. If you are not positive that the book belongs to this author, please don't include it.
                    {format}
                    """;
            BeanOutputConverter<Author> outputParser = new BeanOutputConverter<>(Author.class);

            Prompt prompt  = PromptTemplate.builder()
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


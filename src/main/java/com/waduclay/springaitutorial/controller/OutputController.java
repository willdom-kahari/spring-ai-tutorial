package com.waduclay.springaitutorial.controller;


import com.waduclay.springaitutorial.dto.ApiResponse;
import com.waduclay.springaitutorial.dto.Author;
import com.waduclay.springaitutorial.service.OutputService;
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
    private final OutputService outputService;

    /**
     * Constructs a new OutputController with the provided OutputService.
     * 
     * @param outputService the OutputService used for structured output operations
     */
    public OutputController(OutputService outputService) {
        this.outputService = outputService;
    }

    /**
     * Demonstrates converting AI responses to structured list format.
     * This endpoint uses ListOutputConverter to parse AI-generated text into a List of strings,
     * making it easy to work with enumerated data programmatically.
     * 
     * @param artist the artist name to generate a song list for (1-100 characters, cannot be blank)
     * @return ApiResponse containing a list of songs by the specified artist
     */
    @GetMapping("/songs")
    public ApiResponse<List<String>> generate(@RequestParam(value = "artist", defaultValue = "Taylor Swift") 
                                @NotBlank(message = "Artist name cannot be blank")
                                @Size(min = 1, max = 100, message = "Artist name must be between 1 and 100 characters")
                                String artist){
        return outputService.generateSongsList(artist);
    }

    /**
     * Demonstrates converting AI responses to structured map format.
     * This endpoint uses MapOutputConverter to parse AI-generated text into a Map structure,
     * allowing for key-value pair data representation from unstructured AI responses.
     * 
     * @param author the author name to generate social media links for (1-100 characters, cannot be blank)
     * @return ApiResponse containing a map of author information and social network links
     */
    @GetMapping("/{author}")
    public ApiResponse<Map<String, Object>> generateBooks(@PathVariable(value = "author") 
                                           @NotBlank(message = "Author name cannot be blank")
                                           @Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
                                           String author){
        return outputService.generateAuthorLinks(author);
    }

    /**
     * Demonstrates converting AI responses to structured custom bean format.
     * This endpoint uses BeanOutputConverter to parse AI-generated text into a custom Java object (Author),
     * showcasing the most advanced form of structured output conversion for complex data models.
     * 
     * @param author the author name to generate book information for (1-100 characters, cannot be blank)
     * @return ApiResponse containing an Author object with structured book information
     */
    @GetMapping("/books")
    public ApiResponse<Author> generateBooksVyAuthor(@RequestParam(value = "author", defaultValue = "Ken Kousen")
                                       @NotBlank(message = "Author name cannot be blank")
                                       @Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
                                       String author){
        return outputService.generateAuthorBooks(author);
    }

}


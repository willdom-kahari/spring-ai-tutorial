package com.waduclay.springaitutorial.output.controller;


import com.waduclay.springaitutorial.shared.dto.ApiResponse;
import com.waduclay.springaitutorial.output.dto.Author;
import com.waduclay.springaitutorial.output.service.OutputService;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@RequestMapping("/api/v1/output")
@Validated
@Tag(name = "Structured Output", description = "AI response parsing and conversion to structured formats")
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
    @Operation(
        summary = "Generate Songs List",
        description = "Converts AI responses to structured list format using ListOutputConverter. Generates a list of songs by the specified artist."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Successfully generated songs list"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Invalid input parameters"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "503", 
            description = "Output service unavailable"
        )
    })
    @GetMapping("/songs")
    public ApiResponse<List<String>> generate(
            @Parameter(description = "Artist name to generate songs list for", example = "Taylor Swift")
            @RequestParam(value = "artist", defaultValue = "Taylor Swift") 
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
    @Operation(
        summary = "Generate Author Links",
        description = "Converts AI responses to structured map format using MapOutputConverter. Generates author information and social media links."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Successfully generated author links"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Invalid input parameters"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "503", 
            description = "Output service unavailable"
        )
    })
    @GetMapping("/{author}")
    public ApiResponse<Map<String, Object>> generateBooks(
            @Parameter(description = "Author name to generate social media links for", example = "John Doe")
            @PathVariable(value = "author") 
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
    @Operation(
        summary = "Generate Author Books",
        description = "Converts AI responses to structured custom bean format using BeanOutputConverter. Generates an Author object with structured book information."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Successfully generated author books"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Invalid input parameters"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "503", 
            description = "Output service unavailable"
        )
    })
    @GetMapping("/books")
    public ApiResponse<Author> generateBooksByAuthor(
            @Parameter(description = "Author name to generate book information for", example = "Ken Kousen")
            @RequestParam(value = "author", defaultValue = "Ken Kousen")
            @NotBlank(message = "Author name cannot be blank")
            @Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
            String author){
        return outputService.generateAuthorBooks(author);
    }

}


package com.waduclay.springaitutorial.output.service;

import com.waduclay.springaitutorial.output.dto.Author;
import com.waduclay.springaitutorial.shared.dto.ApiResponse;

import java.util.List;
import java.util.Map;

/**
 * Service interface for structured output conversion operations.
 * This interface defines the contract for converting AI responses into structured formats
 * including lists, maps, and custom Java objects. It provides business logic separation
 * for output parsing and conversion operations.
 *
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
public interface OutputService {

    /**
     * Converts AI responses to structured list format.
     * This method uses ListOutputConverter to parse AI-generated text into a List of strings,
     * making it easy to work with enumerated data programmatically.
     *
     * @param artist the artist name to generate a song list for
     * @return ApiResponse containing a list of songs by the specified artist
     */
    ApiResponse<List<String>> generateSongsList(String artist);

    /**
     * Converts AI responses to structured map format.
     * This method uses MapOutputConverter to parse AI-generated text into a Map structure,
     * allowing for key-value pair data representation from unstructured AI responses.
     *
     * @param author the author name to generate social media links for
     * @return ApiResponse containing a map of author information and social network links
     */
    ApiResponse<Map<String, Object>> generateAuthorLinks(String author);

    /**
     * Converts AI responses to structured custom bean format.
     * This method uses BeanOutputConverter to parse AI-generated text into a custom Java object (Author),
     * showcasing the most advanced form of structured output conversion for complex data models.
     *
     * @param author the author name to generate book information for
     * @return ApiResponse containing an Author object with structured book information
     */
    ApiResponse<Author> generateAuthorBooks(String author);
}

package com.waduclay.springaitutorial.output.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Author data transfer object for structured output conversion.
 * This record represents an author with their associated books,
 * used for demonstrating AI response parsing to custom Java objects.
 *
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Schema(description = "Author information with associated books")
public record Author(
        @Schema(description = "The author's full name", example = "Ken Kousen", required = true)
        String author,

        @Schema(description = "List of books written by the author", example = "[\"Modern Java Recipes\", \"Kotlin Cookbook\"]", required = true)
        List<String> books
) {
}

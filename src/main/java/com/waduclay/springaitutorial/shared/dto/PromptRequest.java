package com.waduclay.springaitutorial.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for prompt-based endpoints.
 * This class encapsulates user input for AI generation endpoints
 * and provides validation constraints for prompt data.
 *
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Schema(description = "Request object for AI prompt generation")
public class PromptRequest {

    @NotBlank(message = "Prompt cannot be blank")
    @Size(min = 1, max = 500, message = "Prompt must be between 1 and 500 characters")
    @Schema(description = "Input prompt for AI to process", example = "Tell me a Dad joke", required = true)
    private String prompt;

    /**
     * Default constructor for JSON deserialization.
     */
    public PromptRequest() {
    }

    /**
     * Constructor with prompt parameter.
     *
     * @param prompt the input prompt
     */
    public PromptRequest(String prompt) {
        this.prompt = prompt;
    }

    /**
     * Gets the prompt text.
     *
     * @return the prompt text
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * Sets the prompt text.
     *
     * @param prompt the prompt text to set
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}

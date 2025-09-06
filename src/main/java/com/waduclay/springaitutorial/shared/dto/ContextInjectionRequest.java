package com.waduclay.springaitutorial.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for context injection endpoints.
 * This class encapsulates user input for context injection operations
 * and provides validation constraints for prompt data with context control.
 *
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Schema(description = "Request object for context injection operations")
public class ContextInjectionRequest {

    @NotBlank(message = "Prompt cannot be blank")
    @Size(min = 1, max = 500, message = "Prompt must be between 1 and 500 characters")
    @Schema(description = "Input prompt for AI to process", example = "What sports are being included in the 2024 summer olympics?", required = true)
    private String prompt;

    @Schema(description = "Whether to inject context into the prompt", example = "false")
    private boolean stuffit = false;

    /**
     * Default constructor for JSON deserialization.
     */
    public ContextInjectionRequest() {
    }

    /**
     * Constructor with prompt parameter.
     *
     * @param prompt the input prompt
     */
    public ContextInjectionRequest(String prompt) {
        this.prompt = prompt;
    }

    /**
     * Constructor with prompt and stuffit parameters.
     *
     * @param prompt  the input prompt
     * @param stuffit whether to inject context
     */
    public ContextInjectionRequest(String prompt, boolean stuffit) {
        this.prompt = prompt;
        this.stuffit = stuffit;
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

    /**
     * Gets the stuffit flag.
     *
     * @return true if context should be injected, false otherwise
     */
    public boolean isStuffit() {
        return stuffit;
    }

    /**
     * Sets the stuffit flag.
     *
     * @param stuffit whether to inject context
     */
    public void setStuffit(boolean stuffit) {
        this.stuffit = stuffit;
    }
}

package com.waduclay.springaitutorial.controller;

import com.waduclay.springaitutorial.dto.ApiResponse;
import com.waduclay.springaitutorial.service.OlympicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.IOException;

/**
 * REST controller demonstrating prompt stuffing techniques for contextual AI responses.
 * This controller showcases the technique of "prompt stuffing" - dynamically injecting 
 * relevant context documents into prompts to improve AI response accuracy and relevance.
 * It demonstrates conditional context loading based on user preferences.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@RestController
@RequestMapping("/olympics")
@Validated
public class OlympicController {
    private final OlympicService olympicService;
    
    /**
     * Constructs a new OlympicController with the provided OlympicService.
     * 
     * @param olympicService the OlympicService used for Olympic sports operations
     */
    public OlympicController(OlympicService olympicService) {
        this.olympicService = olympicService;
    }

    /**
     * Generates responses about 2024 Olympic sports with optional context stuffing.
     * This endpoint demonstrates prompt stuffing by conditionally injecting Olympic sports
     * documentation into the prompt context. When stuffit=true, the AI has access to
     * detailed Olympic sports information; when false, it relies only on its training data.
     * 
     * @param message the question about Olympic sports (1-500 characters, cannot be blank)
     * @param stuffit whether to inject Olympic sports context document into the prompt
     * @return ApiResponse containing Olympic sports information with or without context stuffing
     * @throws IOException if there's an error reading the Olympic sports document
     */
    @GetMapping("/2024")
    public ApiResponse<String> get2024OlympicSports(
            @RequestParam(value = "message", defaultValue = "What sports are being includen in the 2024 summer olympics?") 
            @NotBlank(message = "Message cannot be blank")
            @Size(min = 1, max = 500, message = "Message must be between 1 and 500 characters")
            String message,

            @RequestParam(value = "stuffit", defaultValue = "false") boolean stuffit
    ) throws IOException {
        return olympicService.get2024OlympicSports(message, stuffit);
    }
}

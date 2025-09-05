package com.waduclay.springaitutorial.service;

import com.waduclay.springaitutorial.dto.ApiResponse;

import java.io.IOException;

/**
 * Service interface for Olympic sports prompt stuffing operations.
 * This interface defines the contract for Olympic sports functionality, providing a clean
 * separation between controller logic and business logic for contextual AI responses
 * that demonstrate prompt stuffing techniques with Olympic sports documentation.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
public interface OlympicService {
    
    /**
     * Generates responses about 2024 Olympic sports with optional context stuffing.
     * This method demonstrates prompt stuffing by conditionally injecting Olympic sports
     * documentation into the prompt context. When stuffit=true, the AI has access to
     * detailed Olympic sports information; when false, it relies only on its training data.
     * 
     * @param message the question about Olympic sports
     * @param stuffit whether to inject Olympic sports context document into the prompt
     * @return ApiResponse containing Olympic sports information with or without context stuffing
     * @throws IOException if there's an error reading the Olympic sports document
     */
    ApiResponse<String> get2024OlympicSports(String message, boolean stuffit) throws IOException;
}

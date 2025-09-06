package com.waduclay.springaitutorial.shared.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.util.List;
import java.util.Arrays;

/**
 * Input sanitization utility for preventing prompt injection attacks.
 * This component provides methods to sanitize and validate user inputs before
 * they are processed by AI services. It helps prevent malicious prompt injection
 * attacks that could manipulate AI responses or expose sensitive information.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Component
public class InputSanitizer {
    
    private static final Logger log = LoggerFactory.getLogger(InputSanitizer.class);
    
    /** Maximum allowed input length to prevent resource exhaustion */
    private static final int MAX_INPUT_LENGTH = 2000;
    
    /** Patterns that indicate potential prompt injection attempts */
    private static final List<Pattern> INJECTION_PATTERNS = Arrays.asList(
        Pattern.compile("(?i)ignore\\s+(previous|all)\\s+(instructions?|prompts?)", Pattern.DOTALL),
        Pattern.compile("(?i)forget\\s+(everything|all|previous)", Pattern.DOTALL),
        Pattern.compile("(?i)new\\s+(instructions?|prompts?)\\s*:", Pattern.DOTALL),
        Pattern.compile("(?i)system\\s*:\\s*you\\s+are", Pattern.DOTALL),
        Pattern.compile("(?i)assistant\\s*:\\s*", Pattern.DOTALL),
        Pattern.compile("(?i)user\\s*:\\s*", Pattern.DOTALL),
        Pattern.compile("(?i)\\[\\s*system\\s*\\]", Pattern.DOTALL),
        Pattern.compile("(?i)\\{\\{.*system.*\\}\\}", Pattern.DOTALL),
        Pattern.compile("(?i)act\\s+as\\s+(a\\s+)?different", Pattern.DOTALL),
        Pattern.compile("(?i)pretend\\s+(you\\s+are|to\\s+be)", Pattern.DOTALL),
        Pattern.compile("(?i)jailbreak", Pattern.DOTALL),
        Pattern.compile("(?i)roleplay\\s+as", Pattern.DOTALL)
    );
    
    /** Suspicious keywords that may indicate injection attempts */
    private static final List<String> SUSPICIOUS_KEYWORDS = Arrays.asList(
        "jailbreak", "dan mode", "developer mode", "god mode", "admin mode",
        "root access", "bypass", "override", "unrestricted", "uncensored"
    );
    
    /** Characters that should be escaped or filtered */
    private static final Pattern SPECIAL_CHARS = Pattern.compile("[\\{\\}\\[\\]<>\\$\\#\\@]");
    
    /**
     * Sanitizes user input by removing potentially harmful content and validating length.
     * This method performs comprehensive input sanitization including length validation,
     * pattern matching for injection attempts, and character filtering.
     * 
     * @param input the user input to sanitize
     * @return sanitized input string
     * @throws SecurityException if input contains dangerous patterns or exceeds limits
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        
        // Log original input length for monitoring
        log.debug("Sanitizing input of length: {}", input.length());
        
        // Validate input length
        if (input.length() > MAX_INPUT_LENGTH) {
            log.warn("Input exceeds maximum length: {} > {}", input.length(), MAX_INPUT_LENGTH);
            throw new SecurityException("Input exceeds maximum allowed length of " + MAX_INPUT_LENGTH + " characters");
        }
        
        // Check for injection patterns
        validateAgainstInjectionPatterns(input);
        
        // Check for suspicious keywords
        validateAgainstSuspiciousKeywords(input);
        
        // Sanitize special characters
        String sanitized = sanitizeSpecialCharacters(input);
        
        // Normalize whitespace
        sanitized = normalizeWhitespace(sanitized);
        
        // Final validation
        if (sanitized.trim().isEmpty()) {
            throw new SecurityException("Input cannot be empty after sanitization");
        }
        
        log.debug("Input sanitization completed successfully");
        return sanitized;
    }
    
    /**
     * Validates input against known prompt injection patterns.
     * 
     * @param input the input to validate
     * @throws SecurityException if injection patterns are detected
     */
    private void validateAgainstInjectionPatterns(String input) {
        for (Pattern pattern : INJECTION_PATTERNS) {
            if (pattern.matcher(input).find()) {
                log.warn("Potential prompt injection detected with pattern: {}", pattern.pattern());
                throw new SecurityException("Input contains potentially dangerous injection patterns");
            }
        }
    }
    
    /**
     * Validates input against suspicious keywords that may indicate malicious intent.
     * 
     * @param input the input to validate
     * @throws SecurityException if suspicious keywords are detected
     */
    private void validateAgainstSuspiciousKeywords(String input) {
        String lowerInput = input.toLowerCase();
        for (String keyword : SUSPICIOUS_KEYWORDS) {
            if (lowerInput.contains(keyword)) {
                log.warn("Suspicious keyword detected: {}", keyword);
                throw new SecurityException("Input contains suspicious keywords that are not allowed");
            }
        }
    }
    
    /**
     * Sanitizes special characters that could be used in injection attacks.
     * 
     * @param input the input to sanitize
     * @return input with special characters escaped or removed
     */
    private String sanitizeSpecialCharacters(String input) {
        // Remove or escape special characters that could be used maliciously
        return SPECIAL_CHARS.matcher(input).replaceAll("");
    }
    
    /**
     * Normalizes whitespace by removing excessive spaces and controlling line breaks.
     * 
     * @param input the input to normalize
     * @return input with normalized whitespace
     */
    private String normalizeWhitespace(String input) {
        // Replace multiple spaces with single space
        String normalized = input.replaceAll("\\s+", " ");
        
        // Limit consecutive line breaks
        normalized = normalized.replaceAll("\\n{3,}", "\n\n");
        
        return normalized.trim();
    }
    
    /**
     * Validates if input is safe for AI processing without modification.
     * This method performs read-only validation without sanitizing the input.
     * 
     * @param input the input to validate
     * @return true if input is safe, false otherwise
     */
    public boolean isInputSafe(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        try {
            if (input.length() > MAX_INPUT_LENGTH) {
                return false;
            }
            
            // Check injection patterns
            for (Pattern pattern : INJECTION_PATTERNS) {
                if (pattern.matcher(input).find()) {
                    return false;
                }
            }
            
            // Check suspicious keywords
            String lowerInput = input.toLowerCase();
            for (String keyword : SUSPICIOUS_KEYWORDS) {
                if (lowerInput.contains(keyword)) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            log.warn("Error during input safety validation: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the maximum allowed input length.
     * 
     * @return maximum input length in characters
     */
    public int getMaxInputLength() {
        return MAX_INPUT_LENGTH;
    }
    
    /**
     * Provides a safe truncated version of input for logging purposes.
     * This method ensures sensitive information is not exposed in logs.
     * 
     * @param input the input to truncate for logging
     * @return safely truncated input for logging
     */
    public String getSafeLogString(String input) {
        if (input == null) {
            return "null";
        }
        
        if (input.length() <= 50) {
            return input;
        }
        
        return input.substring(0, 47) + "...";
    }
}

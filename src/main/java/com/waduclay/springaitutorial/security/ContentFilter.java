package com.waduclay.springaitutorial.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Content filtering utility for detecting inappropriate content in requests and responses.
 * This component provides methods to filter offensive language, harmful content, and 
 * inappropriate material before it's processed by AI services or returned to users.
 * It helps maintain content quality and compliance with usage policies.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Component
public class ContentFilter {
    
    private static final Logger log = LoggerFactory.getLogger(ContentFilter.class);
    
    /** Patterns for detecting profanity and offensive language */
    private static final List<Pattern> PROFANITY_PATTERNS = Arrays.asList(
        Pattern.compile("(?i)\\b(damn|hell|crap|stupid|idiot|moron)\\b"),
        Pattern.compile("(?i)\\b(hate|kill|die|murder|violence)\\b"),
        Pattern.compile("(?i)\\b(sex|porn|adult|explicit)\\b")
    );
    
    /** Keywords that indicate potentially harmful content */
    private static final List<String> HARMFUL_KEYWORDS = Arrays.asList(
        "bomb", "weapon", "drug", "illegal", "hack", "exploit", "virus", 
        "malware", "scam", "fraud", "steal", "piracy", "terrorism"
    );
    
    /** Patterns for detecting personal information that should be filtered */
    private static final List<Pattern> PII_PATTERNS = Arrays.asList(
        Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b"), // SSN pattern
        Pattern.compile("\\b\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}\\b"), // Credit card pattern
        Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b"), // Email pattern
        Pattern.compile("\\b\\d{3}-\\d{3}-\\d{4}\\b") // Phone number pattern
    );
    
    /** Patterns for detecting spam-like content */
    private static final List<Pattern> SPAM_PATTERNS = Arrays.asList(
        Pattern.compile("(?i)\\b(buy now|click here|free money|get rich|lottery|winner)\\b"),
        Pattern.compile("(?i)\\b(viagra|cialis|penis|enlargement)\\b"),
        Pattern.compile("(?i)\\b(urgent|limited time|act now|don't wait)\\b")
    );
    
    /**
     * Content filter result containing the filtering decision and details.
     */
    public static class FilterResult {
        private final boolean blocked;
        private final String reason;
        private final String filteredContent;
        private final ContentViolationType violationType;
        
        public FilterResult(boolean blocked, String reason, String filteredContent, ContentViolationType violationType) {
            this.blocked = blocked;
            this.reason = reason;
            this.filteredContent = filteredContent;
            this.violationType = violationType;
        }
        
        public boolean isBlocked() { return blocked; }
        public String getReason() { return reason; }
        public String getFilteredContent() { return filteredContent; }
        public ContentViolationType getViolationType() { return violationType; }
    }
    
    /**
     * Enumeration of content violation types.
     */
    public enum ContentViolationType {
        PROFANITY("Offensive language detected"),
        HARMFUL_CONTENT("Harmful content detected"),
        PERSONAL_INFO("Personal information detected"),
        SPAM("Spam-like content detected"),
        NONE("No violations detected");
        
        private final String description;
        
        ContentViolationType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Filters content for inappropriate material and returns filtering result.
     * This method performs comprehensive content filtering including profanity detection,
     * harmful content identification, personal information protection, and spam detection.
     * 
     * @param content the content to filter
     * @return FilterResult containing the filtering decision and processed content
     */
    public FilterResult filterContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return new FilterResult(false, "Content is empty", content, ContentViolationType.NONE);
        }
        
        log.debug("Filtering content of length: {}", content.length());
        
        // Check for profanity
        FilterResult profanityResult = checkProfanity(content);
        if (profanityResult.isBlocked()) {
            return profanityResult;
        }
        
        // Check for harmful content
        FilterResult harmfulResult = checkHarmfulContent(content);
        if (harmfulResult.isBlocked()) {
            return harmfulResult;
        }
        
        // Check for personal information
        FilterResult piiResult = checkPersonalInformation(content);
        if (piiResult.isBlocked()) {
            return piiResult;
        }
        
        // Check for spam
        FilterResult spamResult = checkSpamContent(content);
        if (spamResult.isBlocked()) {
            return spamResult;
        }
        
        // Content passed all filters
        log.debug("Content filtering completed - no violations detected");
        return new FilterResult(false, "Content approved", content, ContentViolationType.NONE);
    }
    
    /**
     * Checks content for profanity and offensive language.
     * 
     * @param content the content to check
     * @return FilterResult indicating if content should be blocked
     */
    private FilterResult checkProfanity(String content) {
        for (Pattern pattern : PROFANITY_PATTERNS) {
            if (pattern.matcher(content).find()) {
                log.warn("Profanity detected in content");
                String filteredContent = pattern.matcher(content).replaceAll("***");
                return new FilterResult(true, "Content contains inappropriate language", 
                                      filteredContent, ContentViolationType.PROFANITY);
            }
        }
        return new FilterResult(false, null, content, ContentViolationType.NONE);
    }
    
    /**
     * Checks content for harmful keywords and content.
     * 
     * @param content the content to check
     * @return FilterResult indicating if content should be blocked
     */
    private FilterResult checkHarmfulContent(String content) {
        String lowerContent = content.toLowerCase();
        for (String keyword : HARMFUL_KEYWORDS) {
            if (lowerContent.contains(keyword)) {
                log.warn("Harmful content detected: {}", keyword);
                return new FilterResult(true, "Content contains potentially harmful material: " + keyword, 
                                      content, ContentViolationType.HARMFUL_CONTENT);
            }
        }
        return new FilterResult(false, null, content, ContentViolationType.NONE);
    }
    
    /**
     * Checks content for personal information that should be protected.
     * 
     * @param content the content to check
     * @return FilterResult indicating if content should be blocked or filtered
     */
    private FilterResult checkPersonalInformation(String content) {
        String filteredContent = content;
        boolean foundPII = false;
        
        for (Pattern pattern : PII_PATTERNS) {
            if (pattern.matcher(content).find()) {
                log.warn("Personal information detected in content");
                filteredContent = pattern.matcher(filteredContent).replaceAll("[REDACTED]");
                foundPII = true;
            }
        }
        
        if (foundPII) {
            return new FilterResult(false, "Personal information redacted from content", 
                                  filteredContent, ContentViolationType.PERSONAL_INFO);
        }
        
        return new FilterResult(false, null, content, ContentViolationType.NONE);
    }
    
    /**
     * Checks content for spam-like patterns.
     * 
     * @param content the content to check
     * @return FilterResult indicating if content should be blocked
     */
    private FilterResult checkSpamContent(String content) {
        for (Pattern pattern : SPAM_PATTERNS) {
            if (pattern.matcher(content).find()) {
                log.warn("Spam-like content detected");
                return new FilterResult(true, "Content appears to be spam or promotional material", 
                                      content, ContentViolationType.SPAM);
            }
        }
        return new FilterResult(false, null, content, ContentViolationType.NONE);
    }
    
    /**
     * Performs a quick safety check on content without detailed filtering.
     * This method provides a fast way to determine if content is likely safe.
     * 
     * @param content the content to check
     * @return true if content appears safe, false otherwise
     */
    public boolean isContentSafe(String content) {
        if (content == null || content.trim().isEmpty()) {
            return true;
        }
        
        try {
            FilterResult result = filterContent(content);
            return !result.isBlocked() || result.getViolationType() == ContentViolationType.PERSONAL_INFO;
        } catch (Exception e) {
            log.warn("Error during content safety check: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets content filtering statistics for monitoring purposes.
     * 
     * @return basic filtering configuration information
     */
    public String getFilteringInfo() {
        return String.format("Content Filter - Patterns: Profanity=%d, Harmful=%d, PII=%d, Spam=%d", 
                           PROFANITY_PATTERNS.size(), HARMFUL_KEYWORDS.size(), 
                           PII_PATTERNS.size(), SPAM_PATTERNS.size());
    }
}

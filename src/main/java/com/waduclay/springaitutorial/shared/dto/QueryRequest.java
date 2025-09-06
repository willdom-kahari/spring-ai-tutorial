package com.waduclay.springaitutorial.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for query-based endpoints.
 * This class encapsulates user input for search and RAG endpoints
 * and provides validation constraints for query data.
 *
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Schema(description = "Request object for query-based operations")
public class QueryRequest {

    @NotBlank(message = "Query cannot be blank")
    @Size(min = 1, max = 500, message = "Query must be between 1 and 500 characters")
    @Schema(description = "Input query for processing", example = "What services do you offer?", required = true)
    private String query;

    @Schema(description = "Number of top results to return", example = "5")
    private Integer topK;

    /**
     * Default constructor for JSON deserialization.
     */
    public QueryRequest() {
    }

    /**
     * Constructor with query parameter.
     *
     * @param query the input query
     */
    public QueryRequest(String query) {
        this.query = query;
    }

    /**
     * Constructor with query and topK parameters.
     *
     * @param query the input query
     * @param topK  the number of top results to return
     */
    public QueryRequest(String query, Integer topK) {
        this.query = query;
        this.topK = topK;
    }

    /**
     * Gets the query text.
     *
     * @return the query text
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query text.
     *
     * @param query the query text to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Gets the topK value.
     *
     * @return the topK value
     */
    public Integer getTopK() {
        return topK;
    }

    /**
     * Sets the topK value.
     *
     * @param topK the topK value to set
     */
    public void setTopK(Integer topK) {
        this.topK = topK;
    }
}

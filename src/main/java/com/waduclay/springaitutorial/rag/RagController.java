package com.waduclay.springaitutorial.rag;


import com.waduclay.springaitutorial.dto.ApiResponse;
import com.waduclay.springaitutorial.exception.AIServiceException;
import com.waduclay.springaitutorial.exception.VectorStoreException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@RestController
@Validated
public class RagController {
    private static final int DEFAULT_TOP_K = 2;
    
    @Value("classpath:prompts/rag-prompt-template.st")
    private Resource ragTemplate;
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    public RagController(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }


    @GetMapping("/faq")
    public ApiResponse<String> generate(@RequestParam(value = "message", defaultValue = "Tell me a Dad joke") 
                          @NotBlank(message = "Message cannot be blank")
                          @Size(min = 1, max = 500, message = "Message must be between 1 and 500 characters")
                          String message){
        try {
            List<Document> similarDocs = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(message)
                            .topK(DEFAULT_TOP_K)
                            .build()
            );
            List<String> contentList = similarDocs.stream().map(Document::getText).toList();
            Prompt prompt = PromptTemplate.builder()
                    .resource(ragTemplate)
                    .variables(Map.of("input", message, "documents", String.join("\n", contentList)))
                    .build()
                    .create();
            String response = chatClient.prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();
            return ApiResponse.success(response, "RAG response generated successfully");
        } catch (Exception e) {
            if (e.getMessage().contains("vector") || e.getMessage().contains("search")) {
                throw new VectorStoreException("Failed to search vector store: " + e.getMessage(), e);
            } else {
                throw new AIServiceException("Failed to generate RAG response: " + e.getMessage(), e);
            }
        }
    }
}

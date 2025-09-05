package com.waduclay.springaitutorial.rag;


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

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@RestController
public class RagController {
    @Value("classpath:prompts/rag-prompt-template.st")
    private Resource ragTemplate;
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    public RagController(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }


    @GetMapping("/faq")
    public String generate(@RequestParam(value = "message", defaultValue = "Tell me a Dad joke") String message){
        List<Document> similarDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(2)
                        .build()
        );
        List<String> contentList = similarDocs.stream().map(Document::getText).toList();
        Prompt prompt = PromptTemplate.builder()
                .resource(ragTemplate)
                .variables(Map.of("input", message, "documents", String.join("\n", contentList)))
                .build()
                .create();
        return chatClient.prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
    }
}

package com.waduclay.springaitutorial.prompting;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@RestController
public class SimplePromptController {
    private final ChatClient chatClient;
    @Value("classpath:prompts/youtube.st")
    private Resource youTubePromptTemplate;

    public SimplePromptController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/simple-prompt")
    public String simplePrompt() {
        Prompt prompt = new Prompt(new UserMessage("Tell me a dad joke"));
        return chatClient.prompt(prompt)

                .call()
                .content();
    }

    @GetMapping("/yt")
    public String simplePromptTemplate(@RequestParam(value = "genre", defaultValue = "tech") String genre) {
        String message = """
                List 10 of the most popular Youtubers in {genre} along with their current subscriber counts.
                If you don't know the answer, just say "I don't know".
                """;
        PromptTemplate template = PromptTemplate.builder()
                .template(message)
                .variables(Map.of("genre", genre))
                .build();
        Prompt prompt = template.create();
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    @GetMapping("/yt-ex")
    public String simpleExtPromptTemplate(@RequestParam(value = "genre", defaultValue = "tech") String genre) {

        PromptTemplate template = PromptTemplate.builder()
                .resource(youTubePromptTemplate)
                .variables(Map.of("genre", genre))
                .build();
        Prompt prompt = template.create();
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    @GetMapping("/jokes")
    public String dadJoke() {
        SystemMessage systemMessage = new SystemMessage("You are a world class comedian. Your task is to tell dad jokes. If someone asks you about any other jokes, tell them you only ");
        UserMessage us = new UserMessage("Tell me a serious joke about the universe");
        Prompt prompt = new Prompt(systemMessage, us);
        return chatClient.prompt(prompt)

                .call()
                .content();
    }
}

package com.waduclay.springaitutorial.promptstuffing;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@RestController
@RequestMapping("/olympics")
public class OlympicController {
    private final ChatClient chatClient;
    @Value("classpath:prompts/olympic-sports.st")
    private Resource promptTemplate;
    @Value("classpath:docs/olympic-sports.txt")
    private Resource olympicSports;
    public OlympicController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/2024")
    public String get2024OlympicSports(
            @RequestParam(value = "message", defaultValue = "What sports are being includen in the 2024 summer olympics?") String message,

            @RequestParam(value = "stuffit", defaultValue = "false") boolean stuffit
    ) throws IOException {
        PromptTemplate template = PromptTemplate.builder()
                .resource(promptTemplate)
                .variables(Map.of("question", message, "context",""))
                .build();

        if (stuffit) {
            template.add("context", olympicSports.getContentAsString(Charset.defaultCharset()) );
        }
        Prompt prompt = template.create();
        return chatClient.prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
    }
}

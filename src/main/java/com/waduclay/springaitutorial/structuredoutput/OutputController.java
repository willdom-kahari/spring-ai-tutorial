package com.waduclay.springaitutorial.structuredoutput;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@RestController
@RequestMapping("/output")
public class OutputController {
    private final ChatClient chatClient;

    public OutputController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/songs")
    //list output converter is used to parse the output
    public List<String> generate(@RequestParam(value = "artist", defaultValue = "Taylor Swift") String artist){
        String message = """
                Please give me a list of the top 10 songs by {artist}. If you don't know the answer, just say "I don't know".
                {format}
                """;

        ListOutputConverter outputParser = new ListOutputConverter();
        Prompt prompt  = PromptTemplate.builder()
                .template(message)
                .variables(Map.of("artist", artist, "format", outputParser.getFormat()))
                .build()
                .create();

        String content = chatClient.prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
        return outputParser.convert(content);
    }

    @GetMapping("/{author}")
    public Map<String, Object> generateBooks(@PathVariable(value = "author") String author){
        String message = """
                Generate a list of links for the author {author}. Include the author's name as the key and any social network links as the object.
                {format}
                """;
        MapOutputConverter outputParser = new MapOutputConverter();

        Prompt prompt  = PromptTemplate.builder()
                .template(message)
                .variables(Map.of("author", author, "format", outputParser.getFormat()))
                .build()
                .create();

        String content = chatClient.prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
        return outputParser.convert(content);

    }

    @GetMapping("/books")
    public Author generateBooksVyAuthor(@RequestParam(value = "author", defaultValue = "Ken Kousen") String author){
        String message = """
                Generate a list of books written by the author {author}. If you are not positive that the book belongs to this author, please don't include it.
                {format}
                """;
        BeanOutputConverter<Author> outputParser = new BeanOutputConverter<>(Author.class);

        Prompt prompt  = PromptTemplate.builder()
                .template(message)
                .variables(Map.of("author", author, "format", outputParser.getFormat()))
                .build()
                .create();

        String content = chatClient.prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
        return outputParser.convert(content);

    }

}


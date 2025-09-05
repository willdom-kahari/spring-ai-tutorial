package com.waduclay.springaitutorial.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.PostConstruct;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Configuration
public class RagConfiguration {
    @Value("classpath:docs/consultancy-faq.txt")
    private Resource visaFaq;
    @Value("vectorstore.json")
    private String vectorStoreName;
    private static final Logger log = LoggerFactory.getLogger(RagConfiguration.class);
    
    private SimpleVectorStore vectorStoreInstance;
    private File vectorStoreFile;

    @Bean
    public SimpleVectorStore simpleVectorStore(OllamaEmbeddingModel embeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        this.vectorStoreFile = getVectorStoreFile();
        
        if (vectorStoreFile.exists()){
            log.info("Loading vector store from file: {}", vectorStoreFile);
            vectorStore.load(vectorStoreFile);
        } else {
            log.info("Vector store does not exists");
            TextReader textReader = new TextReader(visaFaq);
            textReader.getCustomMetadata().put("filename", "consultancy-faq.txt");
            List<Document> documents = textReader.get();
            TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
            List<Document> splitDocs = tokenTextSplitter.apply(documents);
            vectorStore.add(splitDocs);
            // Ensure parent directory exists before saving
            File parentDir = vectorStoreFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                if (!created) {
                    log.warn("Could not create directory for vector store: {}", parentDir);
                }
            }
            vectorStore.save(vectorStoreFile);
        }
        
        // Store reference for cleanup
        this.vectorStoreInstance = vectorStore;
        return vectorStore;
    }
    
    /**
     * Ensures proper cleanup of vector store resources on application shutdown.
     * This method saves the current state of the vector store to disk before
     * the application terminates, preventing data loss.
     */
    @PreDestroy
    public void cleanupVectorStore() {
        if (vectorStoreInstance != null && vectorStoreFile != null) {
            try {
                log.info("Saving vector store on shutdown: {}", vectorStoreFile);
                vectorStoreInstance.save(vectorStoreFile);
                log.info("Vector store successfully saved on shutdown");
            } catch (Exception e) {
                log.error("Failed to save vector store on shutdown: {}", e.getMessage(), e);
            }
        }
    }

    private File getVectorStoreFile(){
        Path path = Paths.get("data");
        String absolutePath = path.toFile().getAbsolutePath() + File.separator + vectorStoreName;
        return new File(absolutePath);
    }
}

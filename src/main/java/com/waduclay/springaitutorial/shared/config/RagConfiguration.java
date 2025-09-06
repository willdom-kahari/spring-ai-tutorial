package com.waduclay.springaitutorial.shared.config;


import jakarta.annotation.PreDestroy;
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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * Configuration class for Retrieval-Augmented Generation (RAG) functionality.
 * This configuration sets up the vector store infrastructure for semantic document search,
 * including document processing, text splitting, embedding generation, and persistent storage.
 *
 * <p>Key Features:</p>
 * <ul>
 *   <li>Automatic document loading and processing from resource files</li>
 *   <li>Text splitting for optimal chunk sizes and search performance</li>
 *   <li>Vector embedding generation using Ollama embedding model</li>
 *   <li>Persistent storage with automatic save/load functionality</li>
 *   <li>Graceful cleanup on application shutdown</li>
 * </ul>
 *
 * <p>Usage Example:</p>
 * <pre>
 * // The vector store is automatically injected where needed:
 * {@code @Autowired}
 * private SimpleVectorStore vectorStore;
 *
 * // Perform similarity search:
 * List&lt;Document&gt; similarDocs = vectorStore.similaritySearch(
 *     SearchRequest.builder()
 *         .query("What services do you offer?")
 *         .topK(5)
 *         .build()
 * );
 * </pre>
 *
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Configuration
public class RagConfiguration {
    private static final Logger log = LoggerFactory.getLogger(RagConfiguration.class);
    @Value("classpath:docs/consultancy-faq.txt")
    private Resource faqResource;
    @Value("vectorstore.json")
    private String vectorStoreName;
    private SimpleVectorStore vectorStoreInstance;
    private File vectorStoreFile;

    /**
     * Creates and configures a SimpleVectorStore bean for semantic document search.
     * This method handles the complete lifecycle of vector store initialization including:
     * document loading, text splitting, embedding generation, and persistent storage.
     *
     * <p>The method implements smart loading logic:</p>
     * <ul>
     *   <li>If a persisted vector store exists, it loads from disk for faster startup</li>
     *   <li>If no store exists, it processes documents from scratch and persists the result</li>
     * </ul>
     *
     * <p>Document Processing Pipeline:</p>
     * <ol>
     *   <li>Load FAQ documents from classpath resources</li>
     *   <li>Split large documents into optimal chunks (800 tokens with 400 overlap)</li>
     *   <li>Generate vector embeddings using Ollama embedding model</li>
     *   <li>Store embeddings in vector database for similarity search</li>
     *   <li>Persist to disk for future application starts</li>
     * </ol>
     *
     * <p>Usage in Services:</p>
     * <pre>
     * // Inject the vector store:
     * {@code @Autowired}
     * private SimpleVectorStore vectorStore;
     *
     * // Perform similarity search:
     * List&lt;Document&gt; results = vectorStore.similaritySearch(
     *     SearchRequest.builder()
     *         .query("What are your consulting services?")
     *         .topK(3)
     *         .similarityThreshold(0.7)
     *         .build()
     * );
     *
     * // Extract content from results:
     * List&lt;String&gt; content = results.stream()
     *     .map(Document::getText)
     *     .collect(Collectors.toList());
     * </pre>
     *
     * @param embeddingModel the Ollama embedding model for generating vector embeddings
     * @return configured SimpleVectorStore ready for semantic search operations
     */
    @Bean
    public SimpleVectorStore simpleVectorStore(OllamaEmbeddingModel embeddingModel) {
        // Initialize vector store with Ollama embedding model for semantic similarity calculations
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        this.vectorStoreFile = getVectorStoreFile();

        // Check if persisted vector store exists to avoid reprocessing documents
        if (vectorStoreFile.exists()) {
            log.info("Loading vector store from file: {}", vectorStoreFile);
            // Load pre-computed embeddings from disk for faster startup
            vectorStore.load(vectorStoreFile);
        } else {
            log.info("Vector store does not exists");

            // Initialize document reader for FAQ text file
            TextReader textReader = new TextReader(faqResource);
            textReader.getCustomMetadata().put("filename", "consultancy-faq.txt");
            List<Document> documents = textReader.get();

            // Configure text splitter with default parameters for optimal chunk sizes
            // Default: 800 tokens per chunk with 400 token overlap for context preservation
            TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

            // Split large documents into smaller chunks suitable for embedding and retrieval
            // This ensures each chunk contains focused, searchable content units
            List<Document> splitDocs = tokenTextSplitter.apply(documents);

            // Generate embeddings and store in vector database for similarity search
            vectorStore.add(splitDocs);

            // Ensure parent directory exists before saving to prevent file system errors
            File parentDir = vectorStoreFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                if (!created) {
                    log.warn("Could not create directory for vector store: {}", parentDir);
                }
            }

            // Persist vector store to disk for future application starts
            vectorStore.save(vectorStoreFile);
        }

        // Store reference for cleanup during application shutdown
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

    private File getVectorStoreFile() {
        Path path = Paths.get("data");
        String absolutePath = path.toFile().getAbsolutePath() + File.separator + vectorStoreName;
        return new File(absolutePath);
    }
}

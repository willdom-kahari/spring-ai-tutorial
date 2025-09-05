package com.waduclay.springaitutorial.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for SpringAI Master Class project.
 * This configuration customizes the Swagger/OpenAPI documentation including
 * API information, contact details, and licensing information.
 * 
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
@Configuration
public class OpenApiConfig {
    
    /**
     * Configures the OpenAPI documentation with custom information.
     * 
     * @return OpenAPI configuration with project details
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SpringAI Master Class API")
                        .version("1.0.0")
                        .description("Comprehensive Spring AI demonstration project showcasing various AI integration patterns including chat completion, RAG, structured output, and prompt engineering techniques.")
                        .contact(new Contact()
                                .name("Willdom Kahari")
                                .email("developer.wadu at gmail.com")
                                .url("https://github.com/willdom-kahari"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}

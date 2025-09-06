# Spring AI Master Class

A comprehensive Spring Boot application demonstrating various AI integration patterns and techniques using Spring AI
framework. This project showcases best practices for building AI-powered REST APIs with proper documentation,
validation, and structured output handling.

## Features

- **AI Chat Integration**: Basic AI chat functionality with customizable prompts
- **Prompt Engineering**: Demonstrations of various prompt engineering techniques
- **RAG (Retrieval-Augmented Generation)**: Contextual AI responses using document similarity search
- **Structured Output Conversion**: Converting AI responses to Lists, Maps, and custom Java objects
- **Olympic Sports Context**: Prompt stuffing techniques with conditional context injection
- **Comprehensive API Documentation**: Full Swagger/OpenAPI documentation
- **Input Validation**: Multi-layer validation with proper error handling
- **Structured Logging**: Comprehensive logging with different levels

## Technology Stack

- **Spring Boot 3.x**: Main application framework
- **Spring AI**: AI integration and chat capabilities
- **Spring Web**: REST API development
- **Spring Validation**: Input validation and constraints
- **Swagger/OpenAPI 3**: API documentation
- **SLF4J/Logback**: Logging framework
- **Gradle**: Build and dependency management

## Prerequisites

- Java 17 or higher
- Gradle 7.x or higher
- AI service API key (configured in application properties)
- IDE with Spring Boot support (IntelliJ IDEA, VS Code, etc.)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/willdom-kahari/spring-ai-tutorial.git
cd spring-ai-tutorial
```

### 2. Configuration

Create or update `src/main/resources/application.properties` with your AI service configuration:

```properties
# Application Configuration
spring.application.name=SpringAI Tutorial
# AI Service Configuration (using OpenRouter)
spring.ai.openai.api-key=${OPENROUTER_KEY}
spring.ai.openai.chat.options.model=qwen/qwen3-235b-a22b:free
spring.ai.openai.base-url=https://openrouter.ai/api
# Using Ollama for embedding since OpenRouter does not support embedding
spring.ai.ollama.embedding.model=mxbai-embed-large

```

**Note**: This project uses OpenRouter API which provides access to various AI models. Set your OpenRouter API key as an
environment variable `OPENROUTER_KEY`.

### 3. Build the Application

```bash
./gradlew build
```

### 4. Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`.

### 5. Access API Documentation

Once the application is running, you can access:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## API Endpoints

### AI Chat (`/api/v1/chat`)

- `POST /api/v1/chat/generate` - Generate AI responses based on provided prompt

### Prompt Engineering (`/api/v1/prompts`)

- `GET /api/v1/prompts/basic` - Simple hardcoded prompt without parameters
- `GET /api/v1/prompts/template?genre={genre}` - YouTube creators using parameterized template
- `GET /api/v1/prompts/external-template?genre={genre}` - YouTube creators using external template file
- `GET /api/v1/prompts/system-message` - Dad jokes with system message constraints
- `POST /api/v1/prompts/context-injection` - Olympic sports with context injection (prompt stuffing)

### RAG Operations (`/api/v1/rag`)

- `POST /api/v1/rag/query` - Generate contextually-aware AI responses using RAG methodology
- `POST /api/v1/rag/search` - Perform document similarity search without AI generation

### Structured Output (`/api/v1/structured-output`)

- `GET /api/v1/structured-output/list?artist={artist}` - Generate songs list (List format)
- `GET /api/v1/structured-output/map?author={author}` - Generate author links (Map format)
- `GET /api/v1/structured-output/bean?author={author}` - Generate author books (Custom object)

## Project Structure

```
├── README.md                           # This file
├── docs/                              # Comprehensive documentation
│   ├── API_USAGE_GUIDE.md            # Detailed API usage examples
│   ├── DEPLOYMENT_GUIDE.md           # Deployment instructions
│   ├── DEVELOPER_GUIDE.md            # Development guidelines
│   └── TROUBLESHOOTING_GUIDE.md      # Common issues and solutions
├── src/
│   ├── main/
│   │   ├── java/com/waduclay/springaitutorial/
│   │   │   ├── chat/                  # Chat and prompt engineering
│   │   │   │   ├── controller/        # Chat controllers
│   │   │   │   └── service/           # Chat services
│   │   │   ├── output/                # Structured output handling
│   │   │   │   ├── controller/        # Output controllers
│   │   │   │   ├── dto/               # Output DTOs
│   │   │   │   └── service/           # Output services
│   │   │   ├── rag/                   # RAG functionality
│   │   │   │   ├── controller/        # RAG controllers
│   │   │   │   ├── repository/        # Vector store repositories
│   │   │   │   └── service/           # RAG services
│   │   │   └── shared/                # Shared components
│   │   │       ├── config/            # Configuration classes
│   │   │       ├── dto/               # Shared DTOs
│   │   │       ├── exception/         # Exception handling
│   │   │       └── security/          # Security components
│   │   └── resources/
│   │       ├── docs/                  # Document resources for RAG
│   │       ├── prompts/               # External prompt templates
│   │       └── application.properties
│   └── test/                          # Unit and integration tests
└── data/                              # Vector store data
```

## Documentation

This project includes comprehensive documentation in the `docs/` folder:

- **[API Usage Guide](docs/API_USAGE_GUIDE.md)**: Detailed examples and use cases for each API endpoint

## Key Components

### Controllers

- **ChatGenerationController**: Basic AI chat interactions and response generation
- **PromptEngineeringController**: Various prompt engineering techniques and templates
- **RagController**: Retrieval-Augmented Generation operations
- **OutputController**: Structured output conversion to lists, maps, and custom objects

### Services

- **ChatService**: Core AI chat functionality with multiple prompt engineering techniques
- **RagService**: RAG operations, vector search, and document similarity matching
- **OutputService**: Structured output conversion using various parsers and converters

### Configuration

- **ChatConfig**: Chat client configuration
- **RagConfiguration**: RAG and vector store configuration

## Usage Examples

### Basic AI Chat

```bash
curl -X POST "http://localhost:8080/api/v1/chat/generate" \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Tell me a joke"}'
```

### Prompt Engineering

```bash
# Simple prompt
curl "http://localhost:8080/api/v1/prompts/basic"

# Templated prompt
curl "http://localhost:8080/api/v1/prompts/template?genre=tech"

# Context injection
curl -X POST "http://localhost:8080/api/v1/prompts/context-injection" \
  -H "Content-Type: application/json" \
  -d '{"prompt": "What sports are in 2024 Olympics?", "stuffit": true}'
```

### RAG Operations

```bash
# RAG query with context
curl -X POST "http://localhost:8080/api/v1/rag/query" \
  -H "Content-Type: application/json" \
  -d '{"query": "What services do you offer?"}'

# Document search
curl -X POST "http://localhost:8080/api/v1/rag/search" \
  -H "Content-Type: application/json" \
  -d '{"query": "consultancy services", "topK": 5}'
```

### Structured Output

```bash
# List output
curl "http://localhost:8080/api/v1/structured-output/list?artist=Taylor Swift"

# Map output  
curl "http://localhost:8080/api/v1/structured-output/map?author=John Doe"

# Bean output
curl "http://localhost:8080/api/v1/structured-output/bean?author=Ken Kousen"
```

## Error Handling

The application provides structured error responses with:

- Proper HTTP status codes
- Descriptive error messages
- Request validation feedback
- Exception details (in development mode)

## Input Validation

Multi-layer validation includes:

- **Controller Level**: Parameter constraints and format validation
- **Service Level**: Business logic validation
- **Custom Validators**: Domain-specific validation rules

## Development

### Building for Production

```bash
./gradlew bootJar
```

The executable JAR will be created in `build/libs/`.

## Troubleshooting

### Common Issues

1. **API Key Not Configured**
    - Ensure your AI service API key is properly set in `application.properties`
    - Check that the key has sufficient permissions

2. **Port Already in Use**
    - Change the port in `application.properties`: `server.port=8081`
    - Or kill the process using port 8080

3. **Vector Store Issues**
    - Ensure the `data/vectorstore.json` file exists
    - Check file permissions and accessibility

4. **Prompt Template Errors**
    - Verify external template files exist in `src/main/resources/prompts/`
    - Check template syntax and variable placeholders

### Debug Mode

Enable debug logging:

```properties
logging.level.com.waduclay.springaitutorial=DEBUG
logging.level.org.springframework.ai=DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Spring AI team for the excellent AI integration framework
- OpenAI for providing the AI capabilities
- Spring Boot community for the robust application framework

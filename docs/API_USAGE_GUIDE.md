# Spring AI Master Class - API Usage Guide

This guide provides comprehensive examples of how to use all API endpoints with curl commands, including
request/response examples and common use cases.

## Table of Contents

1. [Authentication & Base URL](#authentication--base-url)
2. [Chat Generation Endpoints](#chat-generation-endpoints)
3. [Prompt Engineering Endpoints](#prompt-engineering-endpoints)
4. [Structured Output Endpoints](#structured-output-endpoints)
5. [RAG (Retrieval-Augmented Generation) Endpoints](#rag-retrieval-augmented-generation-endpoints)
6. [Error Handling](#error-handling)
7. [Response Format](#response-format)

## Authentication & Base URL

- **Base URL**: `http://localhost:8080`
- **Authentication**: None required for this demo application
- **Content-Type**: `application/json` (for POST requests)

## Chat Generation Endpoints

### Generate AI Response

Generate an AI response using a simple prompt.

**Endpoint**: `POST /api/v1/chat/generate`

**curl Example**:

```bash
curl -X POST "http://localhost:8080/api/v1/chat/generate" \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "Tell me a Dad joke"
  }'
```

**Response**:

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": "Why don't scientists trust atoms? Because they make up everything!"
}
```

## Prompt Engineering Endpoints

### Basic Prompt

Demonstrates the most basic form of AI interaction with a hardcoded prompt.

**Endpoint**: `GET /api/v1/prompts/basic`

**curl Example**:

```bash
curl -X GET "http://localhost:8080/api/v1/prompts/basic"
```

**Response**:

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": "Why did the scarecrow win an award? Because he was outstanding in his field!"
}
```

### Template-Based Prompt

Demonstrates parameterized prompt templates with variable substitution.

**Endpoint**: `GET /api/v1/prompts/template`

**curl Example**:

```bash
curl -X GET "http://localhost:8080/api/v1/prompts/template?genre=gaming"
```

**Response**:

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": "Here are 10 popular gaming YouTubers:\n1. PewDiePie - 111M subscribers\n2. MrBeast Gaming - 35M subscribers\n..."
}
```

### External Template Prompt

Uses external template files for prompt generation.

**Endpoint**: `GET /api/v1/prompts/external-template`

**curl Example**:

```bash
curl -X GET "http://localhost:8080/api/v1/prompts/external-template?genre=tech"
```

**Response**:

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": "Top tech YouTubers:\n1. Marques Brownlee (MKBHD) - 18M subscribers\n2. Unbox Therapy - 18.1M subscribers\n..."
}
```

### System Message Prompt

Demonstrates system message usage to control AI behavior.

**Endpoint**: `GET /api/v1/prompts/system-message`

**curl Example**:

```bash
curl -X GET "http://localhost:8080/api/v1/prompts/system-message"
```

**Response**:

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": "Why did the universe break up with gravity? Because it needed some space! *ba dum tss*"
}
```

### Context Injection

Demonstrates context injection (prompt stuffing) technique.

**Endpoint**: `POST /api/v1/prompts/context-injection`

**curl Example (without context injection)**:

```bash
curl -X POST "http://localhost:8080/api/v1/prompts/context-injection" \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "What sports are being included in the 2024 summer olympics?",
    "stuffit": false
  }'
```

**curl Example (with context injection)**:

```bash
curl -X POST "http://localhost:8080/api/v1/prompts/context-injection" \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "What sports are being included in the 2024 summer olympics?",
    "stuffit": true
  }'
```

**Response (with context)**:

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": "The 2024 Summer Olympics in Paris will feature 32 sports including traditional events like athletics, swimming, and gymnastics, as well as newer additions like skateboarding, sport climbing, surfing, and breaking (breakdancing)..."
}
```

## Structured Output Endpoints

### Generate List Output

Converts AI response to a structured list format.

**Endpoint**: `GET /api/v1/structured-output/list`

**curl Example**:

```bash
curl -X GET "http://localhost:8080/api/v1/structured-output/list?artist=Taylor%20Swift"
```

**Response**:

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": [
    "Shake It Off",
    "Blank Space",
    "Anti-Hero",
    "Love Story",
    "You Belong With Me"
  ]
}
```

### Generate Map Output

Converts AI response to a structured map/object format.

**Endpoint**: `GET /api/v1/structured-output/map`

**curl Example**:

```bash
curl -X GET "http://localhost:8080/api/v1/structured-output/map?author=John%20Doe"
```

**Response**:

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": {
    "twitter": "@johndoe",
    "linkedin": "/in/johndoe",
    "github": "github.com/johndoe",
    "website": "johndoe.com"
  }
}
```

### Generate Bean Output

Converts AI response to a custom Java object (Author bean).

**Endpoint**: `GET /api/v1/structured-output/bean`

**curl Example**:

```bash
curl -X GET "http://localhost:8080/api/v1/structured-output/bean?author=Ken%20Kousen"
```

**Response**:

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": {
    "author": "Ken Kousen",
    "books": [
      "Modern Java Recipes",
      "Kotlin Cookbook",
      "Making Java Groovy"
    ]
  }
}
```

## RAG (Retrieval-Augmented Generation) Endpoints

### RAG Query

Performs retrieval-augmented generation using vector store context.

**Endpoint**: `POST /api/v1/rag/query`

**curl Example**:

```bash
curl -X POST "http://localhost:8080/api/v1/rag/query" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "What services do you offer?"
  }'
```

**Response**:

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": "Based on our documentation, we offer comprehensive consulting services including software architecture design, Spring Boot application development, AI integration consulting, and technical training programs..."
}
```

### Document Search

Performs similarity search without AI generation.

**Endpoint**: `POST /api/v1/rag/search`

**curl Example**:

```bash
curl -X POST "http://localhost:8080/api/v1/rag/search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "consultancy services",
    "topK": 3
  }'
```

**Response**:

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": [
    {
      "content": "Our consultancy services include enterprise software architecture...",
      "similarity": 0.89,
      "metadata": {
        "source": "consultancy-faq.txt",
        "section": "services"
      }
    },
    {
      "content": "We provide technical training and mentoring services...",
      "similarity": 0.82,
      "metadata": {
        "source": "consultancy-faq.txt",
        "section": "training"
      }
    }
  ]
}
```

## Error Handling

All endpoints return standardized error responses with appropriate HTTP status codes.

### Validation Error Example

**Request**:

```bash
curl -X POST "http://localhost:8080/api/v1/chat/generate" \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": ""
  }'
```

**Response** (400 Bad Request):

```json
{
  "success": false,
  "message": "Prompt cannot be blank",
  "data": null
}
```

### Service Unavailable Example

**Response** (503 Service Unavailable):

```json
{
  "success": false,
  "message": "AI service is currently unavailable. Please try again later.",
  "data": null
}
```

## Response Format

All API responses follow the standardized `ApiResponse<T>` format:

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": "Response data varies by endpoint"
}
```

**Response Fields**:

- `success`: Boolean indicating if the request was successful
- `message`: Human-readable message describing the result
- `data`: The actual response data (type varies by endpoint)

## Common Use Cases

### 1. Simple AI Chat

```bash
curl -X POST "http://localhost:8080/api/v1/chat/generate" \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Explain quantum computing in simple terms"}'
```

### 2. Content Generation with Templates

```bash
curl -X GET "http://localhost:8080/api/v1/prompts/template?genre=cooking"
```

### 3. Knowledge Base Query

```bash
curl -X POST "http://localhost:8080/api/v1/rag/query" \
  -H "Content-Type: application/json" \
  -d '{"query": "What are your pricing models?"}'
```

### 4. Structured Data Extraction

```bash
curl -X GET "http://localhost:8080/api/v1/structured-output/bean?author=Martin%20Fowler"
```

## Development Tips

1. **Testing Locally**: Ensure the application is running with `./gradlew bootRun`
2. **Environment Variables**: Set `OPENROUTER_KEY` as needed
3. **Request Validation**: All POST endpoints validate request bodies according to defined constraints
4. **Rate Limiting**: Consider implementing rate limiting for production usage
5. **Monitoring**: Use the provided error responses for debugging and monitoring

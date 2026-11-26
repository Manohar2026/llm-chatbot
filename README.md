# LLM Chatbot

A very basic chatbot built with Java 17, Spring Boot, and the OpenRouter API. Request, response, and conversation-message DTOs use regular Java classes (POJOs), not Java records.

## Features

- Simple chat interface with user messages on the right and AI messages on the left.
- Sends the complete conversation history to the LLM on every request.
- Stores conversation messages in a Java `List<ChatMessage>`.
- Uses OpenAI's `openai/gpt-4o-mini` model through OpenRouter by default.
- No database or additional infrastructure.

## Requirements

- Java 17+
- Maven
- An OpenRouter API key

## Run locally

1. Clone this repository.
2. Set your OpenRouter key as an environment variable. Do not put the key in source code.

Windows PowerShell:

```powershell
$env:OPENROUTER_API_KEY="your-new-openrouter-key"
```

macOS/Linux:

```bash
export OPENROUTER_API_KEY="your-new-openrouter-key"
```

3. From the project root, run:

```bash
mvn spring-boot:run
```

4. Open http://localhost:8080.

## Configuration

The default model is `openai/gpt-4o-mini`. To use another model available on OpenRouter, set:

```bash
OPENROUTER_MODEL="openai/gpt-4o-mini"
```

## How memory works

The Spring service stores messages in an in-memory Java list. Each new user message is added to that list, and the entire list is sent to OpenRouter. The assistant response is then added to the same list.

This is intentionally a single shared conversation for a basic learning project. The conversation is cleared when the application restarts. It is not designed for multiple users or concurrent separate chats.

# LLM Chatbot

A small full-stack chatbot built with Java 17, Spring Boot, Spring AI, and OpenRouter. The frontend is plain HTML, CSS, and JavaScript—no Node.js or frontend build tools are required.

## Project structure

```text
llm-chatbot/
├── frontend/
│   ├── index.html
│   ├── style.css
│   └── app.js
├── src/
│   └── main/
│       ├── java/com/example/chatbot/
│       │   ├── ChatbotApplication.java
│       │   ├── ChatRequest.java
│       │   ├── ChatResponse.java
│       │   ├── controller/ChatController.java
│       │   └── service/ChatService.java
│       └── resources/application.properties
└── pom.xml
```

## Requirements

- Java 17 or later
- Maven
- An OpenRouter API key

## Run locally

1. Open a terminal in the project root (the folder containing `pom.xml`).
2. Set your OpenRouter API key.

Windows PowerShell:

```powershell
$env:OPENROUTER_API_KEY="your-openrouter-api-key"
```

macOS/Linux:

```bash
export OPENROUTER_API_KEY="your-openrouter-api-key"
```

3. Start the Spring Boot application:

```bash
mvn spring-boot:run
```

4. Open [http://localhost:8081](http://localhost:8081).

The Spring Boot application serves the files in the root `frontend/` folder. Keep that folder next to `pom.xml` when running the application.

## Choose a model

The default model is `openai/gpt-4o-mini`. To use another model available on OpenRouter, set the `OPENROUTER_MODEL` environment variable before starting the application.

PowerShell:

```powershell
$env:OPENROUTER_MODEL="openai/gpt-4o-mini"
```

## How it works

- `ChatController` exposes `POST /api/chat` and `DELETE /api/chat`.
- `ChatService` uses Spring AI's `ChatClient` to call the configured model.
- Spring AI's `MessageChatMemoryAdvisor` and in-memory `ChatMemory` keep a bounded message window for each HTTP session.
- The browser session ID is used as the conversation ID, so separate browser sessions do not share the same conversation.
- `DELETE /api/chat` clears the current browser session's conversation.
- Conversation memory is held in memory and is cleared when the application restarts.

The OpenAI model integration is configured to use OpenRouter's OpenAI-compatible API. The API key is read from the `OPENROUTER_API_KEY` environment variable; do not commit API keys to Git.

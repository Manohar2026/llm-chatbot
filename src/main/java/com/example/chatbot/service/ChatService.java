package com.example.chatbot.service;

import com.example.chatbot.ChatMessage;
import com.example.chatbot.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private static final String OPENROUTER_URL =
            "https://openrouter.ai/api/v1/chat/completions";

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    // One in-memory conversation for this simple first version.
    private final List<ChatMessage> messages = new ArrayList<>();

    public ChatService(
            RestClient.Builder restClientBuilder,
            @Value("${OPENROUTER_API_KEY:}") String apiKey,
            @Value("${OPENROUTER_MODEL:openai/gpt-4o-mini}") String model) {
        this.restClient = restClientBuilder.build();
        this.apiKey = apiKey;
        this.model = model;
    }

    public synchronized ChatResponse sendMessage(String userMessage) {
        if (apiKey.isBlank()) {
            throw new IllegalStateException(
                    "OPENROUTER_API_KEY is not configured. Set it as an environment variable.");
        }

        messages.add(new ChatMessage("user", userMessage));

        try {
            // Send the complete conversation, including the latest user message.
            JsonNode response = restClient.post()
                    .uri(OPENROUTER_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("HTTP-Referer", "http://localhost:8080")
                    .header("X-Title", "Simple Java Chatbot")
                    .body(Map.of(
                            "model", model,
                            "messages", messages
                    ))
                    .retrieve()
                    .body(JsonNode.class);

            String reply = response.path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText("");

            if (reply.isBlank()) {
                throw new IllegalStateException("The AI returned an empty response.");
            }

            messages.add(new ChatMessage("assistant", reply));
            return new ChatResponse(reply);

        } catch (Exception exception) {
            // Keep the conversation consistent if the API request fails.
            messages.remove(messages.size() - 1);
            throw new IllegalStateException("Could not get a response from OpenRouter: "
                    + exception.getMessage(), exception);
        }
    }
}

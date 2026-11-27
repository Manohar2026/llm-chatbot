package com.example.chatbot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private List<Message> history = new ArrayList<>();
    private final String apiKey;

    public ChatService(
            ChatClient.Builder chatClientBuilder,
            @Value("${OPENROUTER_API_KEY:}") String apiKey) {
        this.apiKey = apiKey;
        this.chatClient = chatClientBuilder.build();
    }

    public String sendMessage(String userMessage) {
        if (apiKey.isBlank()) {
            throw new IllegalStateException(
                    "OPENROUTER_API_KEY is not configured. Set it as an environment variable.");
        }

        history.add(new UserMessage(userMessage));

        String reply = chatClient.prompt()
                .messages(history)
                .call()
                .content();

        if (reply == null || reply.isBlank()) {
            throw new IllegalStateException("The AI returned an empty response.");
        }

        history.add(new AssistantMessage(reply));

        return reply;
    }


}

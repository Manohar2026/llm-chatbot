package com.example.chatbot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final String apiKey;

    public ChatService(
            ChatClient.Builder chatClientBuilder,
            ChatMemory chatMemory,
            @Value("${OPENROUTER_API_KEY:}") String apiKey) {
        this.chatMemory = chatMemory;
        this.apiKey = apiKey;
        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public String sendMessage(String userMessage, String conversationId) {
        if (apiKey.isBlank()) {
            throw new IllegalStateException(
                    "OPENROUTER_API_KEY is not configured. Set it as an environment variable.");
        }

        String reply = chatClient.prompt()
                .user(userMessage)
                .advisors(advisors -> advisors.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();

        if (reply == null || reply.isBlank()) {
            throw new IllegalStateException("The AI returned an empty response.");
        }

        return reply;
    }

    public void clearConversation(String conversationId) {
        chatMemory.clear(conversationId);
    }
}

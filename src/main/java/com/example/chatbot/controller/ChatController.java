package com.example.chatbot.controller;

import com.example.chatbot.ChatRequest;
import com.example.chatbot.ChatResponse;
import com.example.chatbot.service.ChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<?> chat(
            @RequestBody ChatRequest request) {

        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Message cannot be empty."));
        }

        try {
            String reply = chatService.sendMessage(
                    request.getMessage().trim());

            return ResponseEntity.ok(new ChatResponse(reply));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                    new ErrorResponse(exception.getMessage()));
        }
    }

    public static class ErrorResponse {

        private String error;

        public ErrorResponse() {
        }

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}

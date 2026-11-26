package com.example.chatbot;

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
    public ResponseEntity<?> chat(@RequestBody ChatRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Message cannot be empty."));
        }

        try {
            return ResponseEntity.ok(chatService.sendMessage(request.message().trim()));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                    new ErrorResponse(exception.getMessage()));
        }
    }

    public record ErrorResponse(String error) {
    }
}

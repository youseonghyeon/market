package com.project.market.modules.chat.controller;

import com.project.market.modules.chat.repository.ChatRepository;
import com.project.market.modules.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatRepository chatRepository;

    @GetMapping("/chat")
    public String chatForm() {
        return "chat/chat";
    }

    @MessageMapping("/TTT")
    @SendTo("/topic/message")
    public String receiveMessage(String message) {
        log.info("message={}", message);
        return message;
    }
}

package com.project.market.modules.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class ChatController {

    @GetMapping("/chat")
    public String chatGet() {
        return "chat/chat";
    }

    @GetMapping("/chat/list")
    public String chatList() {
        // 매니저 이상만 접속 가능
        return "chat/chat-list";
    }
}

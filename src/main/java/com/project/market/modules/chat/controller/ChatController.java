package com.project.market.modules.chat.controller;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.chat.entity.Chat;
import com.project.market.modules.chat.repository.ChatRepository;
import com.project.market.modules.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatRepository chatRepository;

    @GetMapping("/chat")
    public String chatGet(@CurrentAccount Account account, Model modal) {
        List<Chat> list = chatRepository.findBySenderOrderBySendDateDesc(account);
        modal.addAttribute("list", list);
        return "chat/chat";
    }

    @GetMapping("/chat/list")
    public String chatList(Model model) {
        // 매니저 이상만 접속 가능
        List<Chat> chatList = chatService.findRecentChat();
        model.addAttribute("chatList", chatList);
        return "chat/chat-list";
    }
}

package com.project.market.modules.chat.controller;

import com.project.market.infra.exception.UnAuthorizedException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.chat.dto.MessageDto;
import com.project.market.modules.chat.entity.Chat;
import com.project.market.modules.chat.repository.ChatRepository;
import com.project.market.modules.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatRepository chatRepository;

    private final SimpMessagingTemplate template;

    @GetMapping("/chat")
    public String chatLink(@CurrentAccount Account account) {
        return "redirect:/chat/" + account.getId();
    }

    @GetMapping("/chat/{mid}")
    public String chatForm(@CurrentAccount Account account, @PathVariable("mid") Long mid, Model model) {
        if (!account.getId().equals(mid)) {
            throw new UnAuthorizedException("접근 권한이 없습니다.");
        }
        List<Chat> previousChattingRecord = new ArrayList<>();
        model.addAttribute(account);
        model.addAttribute("mid", mid);
        return "chat/chat";
    }

    @MessageMapping("/inquiry/{mid}")
    public void receiveInquiry(@DestinationVariable("mid") Long mid, MessageDto message) {
        chatService.saveChat(message);
        template.convertAndSend("/topic/message/" + mid, message.getContent());
    }

//    @MessageMapping("/answer/{mid}")
//    public void sendAnswer(@DestinationVariable("mid") Long mid, MessageDto message) {
//        chatService.saveChat(message);
//        template.convertAndSend("/topic/message/" + mid, message.getContent());
//    }
}

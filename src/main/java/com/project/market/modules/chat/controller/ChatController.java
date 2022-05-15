package com.project.market.modules.chat.controller;

import com.project.market.infra.exception.UnAuthorizedException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.chat.dto.MessageDto;
import com.project.market.modules.chat.entity.Chat;
import com.project.market.modules.chat.repository.ChatRepository;
import com.project.market.modules.chat.service.ChatService;
import com.project.market.modules.order.dao.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatRepository chatRepository;
    private final OrderRepository orderRepository;

    private final SimpMessagingTemplate template;

    @GetMapping("/chat/{roomName}")
    public String chatForm(@CurrentAccount Account account, @PathVariable("roomName") String roomName, Model model) {
        List<Chat> chatRecord = chatRepository.getChatContentsByRoomName(roomName);
        model.addAttribute("chatRecord", chatRecord);
        model.addAttribute("roomName", roomName);
        model.addAttribute("account", account);
        return "chat/chat";
    }

//    @MessageMapping("/inquiry/{roomId}")
    @MessageMapping("/message/{roomName}")
    public void receiveInquiry(@DestinationVariable("roomName") String roomName, MessageDto message) {
        message.setSendDate(LocalDateTime.now());
        chatService.saveChat(message);
        template.convertAndSend("/topic/message/" + roomName, message);
    }


    @GetMapping("/admin/chat-list")
    public String chatList(Model model) {
        List<Chat> chatList = chatRepository.findRecentChat();
        model.addAttribute("chatList", chatList);
        return "chat/chat-list";
    }

}

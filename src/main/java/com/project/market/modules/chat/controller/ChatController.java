package com.project.market.modules.chat.controller;

import com.project.market.infra.exception.UnAuthorizedException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.chat.dto.MessageDto;
import com.project.market.modules.chat.dto.RecordDto;
import com.project.market.modules.chat.entity.Chat;
import com.project.market.modules.chat.repository.ChatRepository;
import com.project.market.modules.chat.service.ChatService;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping("/chat")
    @ResponseBody
    public Long chatLink(@CurrentAccount Account account) {
        // 채팅 방Id를 받는 Controller -> /chat/record/{accountId} 로 연결되어 채팅 기록을 반환
        return account.getId();
    }

    @GetMapping("/chat/record/{roomId}")
    @ResponseBody
    public List<RecordDto> chatForm(@CurrentAccount Account account, @PathVariable("roomId") Long roomId) {
        if (!account.getId().equals(roomId)) {
            throw new UnAuthorizedException("접근 권한이 없습니다.");
        }
        // Dto로 채팅방 기록 전송
        return chatRepository.getChatDtoContentsByRoomId(roomId);

    }

    @MessageMapping("/inquiry/{roomId}")
    public void receiveInquiry(@DestinationVariable("roomId") Long roomId, MessageDto message) {
        message.setSendDate(LocalDateTime.now());
        chatService.saveChat(message);
        template.convertAndSend("/topic/message/" + roomId, message);
    }


    // 어드민을 위한 채팅목록/채팅방
    @GetMapping("/admin/chat-list")
    public String chatList(Model model) {
        List<Chat> chatList = chatRepository.findRecentChat();
        model.addAttribute("chatList", chatList);
        return "chat/chat-list";
    }

    @GetMapping("/admin/chat/{roomId}")
    public String replyForm(@CurrentAccount Account account, @PathVariable("roomId") Long roomId, Model model) {
        List<Order> orders = orderRepository.findByCustomerIdOrderByCreatedDateDesc(roomId);
        List<Chat> chatRecord = chatRepository.getChatContentsByRoomId(roomId);
        model.addAttribute("orderList", orders);
        model.addAttribute("chatRecord", chatRecord);
        model.addAttribute("account", account);
        model.addAttribute("roomId", roomId);
        return "chat/admin-chat";
    }
}

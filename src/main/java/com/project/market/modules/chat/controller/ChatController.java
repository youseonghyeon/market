package com.project.market.modules.chat.controller;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.chat.dto.MessageDto;
import com.project.market.modules.chat.entity.Chat;
import com.project.market.modules.chat.entity.Room;
import com.project.market.modules.chat.repository.ChatRepository;
import com.project.market.modules.chat.repository.RoomRepository;
import com.project.market.modules.chat.service.ChatService;
import com.project.market.modules.chat.service.RoomService;
import com.project.market.modules.item.entity.Item;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatRepository chatRepository;
    private final OrderRepository orderRepository;

    private final SimpMessagingTemplate template;
    private final RoomRepository roomRepository;
    private final RoomService roomService;

    @GetMapping("/chat/join/{itemId}")
    public String chatJoin(@CurrentAccount Account account, @PathVariable("itemId") Item item) {
        Long sellerId = item.getEnrolledBy().getId();
        Long customerId = account.getId();
        Room previousRoom = roomService.findPreviousRoom(sellerId, customerId);
        if (previousRoom != null) {
            return "redirect:/chat/" + previousRoom.getRoomName();
        }
        String newRoomName = UUID.randomUUID().toString().substring(0, 8);
        roomService.createRoom(newRoomName, sellerId, customerId);
        return "redirect:/chat/" + newRoomName;
    }

    @GetMapping("/chat/{roomName}")
    public String chatForm(@CurrentAccount Account account, @PathVariable("roomName") String roomName, Model model) {
        Room room = roomRepository.findByRoomName(roomName);
        if (room == null) {
            log.info("???????????? ???????????? ?????? accountId={}, roomName={}", account.getId(), roomName);
            throw new IllegalStateException("???????????? ???????????? ??????");
        }
        chatValidator(account, room);
        List<Chat> chatRecord = chatRepository.getChatContentsByRoomName(roomName);
        model.addAttribute("chatRecord", chatRecord);
        model.addAttribute("roomName", roomName);
        model.addAttribute("account", account);
        return "chat/chat";
    }

    @GetMapping("/chat/list")
    public String chatListForm(@CurrentAccount Account account, Model model) {
        List<Room> rooms = roomService.findRoomsByUserId(account.getId());
        model.addAttribute("roomList", rooms);
        return "chat/list";
    }

    private void chatValidator(Account account, Room room) {
        // ???????????? ????????? ????????? ????????? ??? ??????
        if (!room.includedUserId(account.getId())) {
            log.info("???????????? ???????????? ????????? ??? ??????(???????????? ?????? ????????? ??????)");
            throw new IllegalStateException("???????????? ?????? ????????? ??????");
        }
    }

    @MessageMapping("/message/{roomName}")
    public void receiveInquiry(@DestinationVariable("roomName") String roomName, MessageDto message) {
        message.setSendDate(LocalDateTime.now());
        chatService.saveChat(message);
        template.convertAndSend("/topic/message/" + roomName, message);
    }

}

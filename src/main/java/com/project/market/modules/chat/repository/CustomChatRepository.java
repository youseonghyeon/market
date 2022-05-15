package com.project.market.modules.chat.repository;

import com.project.market.modules.chat.entity.Chat;

import java.util.List;

public interface CustomChatRepository {
    List<Chat> findRecentChat();

    List<Chat> getChatContentsByRoomName(String roomName);

}

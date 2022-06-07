package com.project.market.modules.chat.repository;

import com.project.market.modules.chat.dto.RecordDto;
import com.project.market.modules.chat.entity.Chat;

import java.util.List;

public interface CustomChatRepository {
    List<Chat> findRecentChat();

    List<Chat> getChatContentsByRoomId(Long customerId);

    List<RecordDto> getChatDtoContentsByRoomId(Long roomId);

}

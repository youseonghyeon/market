package com.project.market.modules.chat.repository;

import com.project.market.modules.chat.dto.ChattingRoomDto;
import com.project.market.modules.chat.dto.RecordDto;
import com.project.market.modules.chat.entity.Chat;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.project.market.modules.account.entity.QAccount.account;
import static com.project.market.modules.chat.entity.QChat.chat;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRepositoryImpl implements CustomChatRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public List<ChattingRoomDto> findRecentChat() {
        List<ChattingRoomDto> dto = queryFactory.select(Projections.constructor(ChattingRoomDto.class, chat.roomId, chat.sendDate.max(), account))
                .from(chat)
                .leftJoin(account)
                .on(chat.roomId.eq(account.id))
                .groupBy(chat.roomId)
                .fetch();
        return dto;
    }

    public List<Chat> getChatContentsByRoomId(Long roomId) {
        return queryFactory.selectFrom(chat)
                .where(chat.roomId.eq(roomId))
                .orderBy(chat.sendDate.asc())
                .fetch();
    }

    public List<RecordDto> getChatDtoContentsByRoomId(Long roomId) {
        List<RecordDto> dto = queryFactory.select(Projections.constructor(RecordDto.class,
                        chat.content, chat.sendDate, account.id,
                        account.nickname, chat.confirmed))
                .from(chat)
                .innerJoin(chat.sender, account)
                .where(chat.roomId.eq(roomId))
                .orderBy(chat.sendDate.asc())
                .fetch();

        return dto;
    }


}

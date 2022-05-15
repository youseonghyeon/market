package com.project.market.modules.chat.repository;

import com.project.market.modules.chat.entity.Chat;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.project.market.modules.chat.entity.QChat.chat;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRepositoryImpl implements CustomChatRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public List<Chat> findRecentChat() {
        String rowNumBySendDateQuery = "select row_number() over (partition by room_id order by send_date desc) as row, * from chat";
        List<Chat> result = em.createNativeQuery("select *  from (" +
                        rowNumBySendDateQuery + ") as c where c.row = 1 order by send_date desc",
                Chat.class).getResultList();
        return result;
    }

    public List<Chat> getChatContentsByRoomName(String roomName) {
        return queryFactory.selectFrom(chat)
                .where(chat.roomName.eq(roomName))
                .orderBy(chat.sendDate.asc())
                .fetch();
    }
}

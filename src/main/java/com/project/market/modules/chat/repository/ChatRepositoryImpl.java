package com.project.market.modules.chat.repository;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.entity.QAccount;
import com.project.market.modules.chat.dto.RecordDto;
import com.project.market.modules.chat.entity.Chat;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
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

    public List<Chat> findRecentChat() {
        // TODO JPQL을 써서 코드를 정리해야함 & querydsl로 풀이 가능한지 찾아봐야 함
//        List<Long> roomIdList = queryFactory.select(chat.roomId)
//                .from(chat)
//                .orderBy(chat.sendDate.desc())
//                .groupBy(chat.roomId).fetch();

        String rowNumBySendDateQuery = "select row_number() over (partition by room_id order by send_date desc) as row, * from chat";
        List<Chat> result = em.createNativeQuery("select *  from (" +
                        rowNumBySendDateQuery + ") as c where c.row = 1 order by send_date desc",
                Chat.class).getResultList();
        return result;
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
                .join(chat.sender, account)
                .where(chat.roomId.eq(roomId))
                .orderBy(chat.sendDate.asc())
                .fetch();
        return dto;
    }
}

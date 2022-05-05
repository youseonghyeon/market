package com.project.market.modules.chat.repository;

import com.project.market.modules.account.entity.Account;
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
        // TODO JPQL을 써서 코드를 정리해야함 & querydsl로 풀이 가능한지 찾아봐야 함
        String rowNumBySendDateQuery = "select row_number() over (partition by sender_account_id order by send_date desc) as row, * from chat";
        List<Chat> result = em.createNativeQuery("select *  from (" +
                        rowNumBySendDateQuery + ") as c where c.row = 1 order by send_date desc",
                Chat.class).getResultList();
        return result;
    }

    public List<Chat> getChatContentByAccount(Long customerId) {
        return queryFactory.selectFrom(chat)
                .where(chat.sender.id.eq(customerId)
                        .or(chat.receiver.id.eq(customerId)))
                .orderBy(chat.sendDate.desc())
                .fetch();
    }
}

package com.project.market.modules.chat.service;

import com.project.market.modules.chat.entity.Chat;
import com.project.market.modules.chat.repository.ChatRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;



    public List<Chat> findRecentChat() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        String rowNumBySendDateQuery = "select row_number() over (partition by sender_account_id order by send_date desc) as row, * from chat";
        List<Chat> result = em.createNativeQuery("select *  from (" +
                        rowNumBySendDateQuery + ") as c where c.row = 1 order by send_date desc",
                Chat.class).getResultList();
        return result;
    }

}

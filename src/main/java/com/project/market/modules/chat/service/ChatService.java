package com.project.market.modules.chat.service;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.chat.dto.MessageDto;
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
    private final AccountRepository accountRepository;
    private final EntityManager em;


    public void saveChat(MessageDto message) {
        Account sender = accountRepository.getByLoginId(message.getSenderId());
        Account receiver = accountRepository.getByLoginId(message.getReceiverId());
        Chat chat = new Chat(sender, receiver, message.getContent());
        chatRepository.save(chat);
    }
}

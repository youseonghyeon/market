package com.project.market.modules.chat.repository;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ChatRepository extends JpaRepository<Chat, Long>, CustomChatRepository {

    List<Chat> findBySenderOrderBySendDateDesc(Account sender);
}

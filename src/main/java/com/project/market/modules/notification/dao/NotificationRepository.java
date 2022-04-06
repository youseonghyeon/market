package com.project.market.modules.notification.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientOrderByCreatedAtDesc(Account recipient);

    long countByRecipientAndConfirmedFalse(Account recipient);

    List<Notification> findByRecipient(Account recipient);
}

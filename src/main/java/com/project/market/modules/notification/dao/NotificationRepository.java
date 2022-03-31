package com.project.market.modules.notification.dao;

import com.project.market.modules.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}

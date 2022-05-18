package com.project.market.modules.notification.service;

import com.project.market.infra.mail.MailSender;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.notification.entity.Notification;
import com.project.market.modules.notification.repository.NotificationRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.project.market.modules.account.entity.QAccount.account;
import static com.project.market.modules.item.entity.QTag.tag;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;
    private final JPAQueryFactory queryFactory;
    private final MailSender mailSender;

    @Async
    public void noticeItemEnrollment(Item item) {
        BooleanExpression condition = account.itemEnrollAlertByWeb.isTrue();
        // BUG FIX : 수신자 중복 제거
        Set<Account> recipients = getAccountsHasTags(item, condition);

        for (Account recipient : recipients) {
            Notification notification = newItemNoticeBuild(item);
            notification.setRecipient(recipient);
            notificationRepository.save(notification);
        }
    }

    public void noticeByEmailItemEnrollment(Item item) {
        BooleanExpression condition = account.itemEnrollAlertByMail.isTrue();
        Set<Account> recipients = getAccountsHasTags(item, condition);

        for (Account recipient : recipients) {
            mailSender.sendNoticeMail(recipient, item);
        }
    }

    private Set<Account> getAccountsHasTags(Item item, BooleanExpression condition) {
        Set<Tag> tags = item.getTags();
        List<Account> accounts = queryFactory.select(account)
                .from(account)
                .join(account.tags, tag)
                .where(
                        condition, // 알림 거부자 제외
                        tag.in(tags)
                )
                .fetch();
        return new HashSet<>(accounts);
    }

    private Notification newItemNoticeBuild(Item item) {
        // TODO admin의 아이디가 변경될 수 있으므로 매니저 아이디를 새로 만들어서 넣어야 함
        Account admin = accountRepository.findByLoginId("admin");
        return Notification.builder()
                .itemId(item.getId())
                .subject("관심 상품이 등록되었습니다.")
                .content("상품 이름: " + item.getName())
                .createdAt(LocalDateTime.now())
                .confirmed(false)
                .sender(admin)
                .build();
    }

    public void confirm(List<Notification> notifications) {
        notifications.forEach(Notification::confirm);
    }
}

package com.project.market.modules.notification.dao;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.notification.entity.Notification;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    @Async
    public void noticeItemEnrollment(Account sender, Item item) {
        List<Tag> tags = item.getTags();
        List<Account> recipients = getAccountsByTags(sender, tags);
        for (Account recipient : recipients) {
            Notification notification = newItemNoticeBuild(item);
            notification.setRecipient(recipient);
            notificationRepository.save(notification);
        }
    }

    private List<Account> getAccountsByTags(Account sender, List<Tag> tags) {
        return queryFactory.select(account)
                .from(account)
                .join(account.tags, tag)
                .where(
                        account.id.ne(sender.getId()), // 상품 등록자 제외
                        account.itemEnrollAlertByWeb.isTrue(), // 알림 거부자 제외
                        tag.in(tags)
                )
                .fetch();
    }

    private Notification newItemNoticeBuild(Item item) {
        // TODO admin의 아이디가 변경될 수 있으므로 매니저 아이디를 새로 만들어서 넣어야 함
        Account admin = accountRepository.findByLoginId("admin");
        return Notification.builder()
                .subject("관심 상품이 등록되었습니다.")
                .content("상품 이름: " + item.getName())
                .createdAt(LocalDateTime.now())
                .confirmed(false)
                .sender(admin)
                .build();
    }
}

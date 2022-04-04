package com.project.market.modules.notification.entity;

import com.project.market.modules.account.entity.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification  {

    @Id
    @GeneratedValue
    @Column(name = "notification_id")
    private Long id;

    private Long itemId;

    private String subject;

    private String content;

    private LocalDateTime createdAt;

    private boolean confirmed;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account sender;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account recipient;

    public void setRecipient(Account account) {
        recipient = account;
        account.getNotifications().add(this);
    }

    public void confirm() {
        confirmed = true;
    }

}

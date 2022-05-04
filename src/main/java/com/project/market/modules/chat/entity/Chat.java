package com.project.market.modules.chat.entity;

import com.project.market.modules.account.entity.Account;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {

    @Id
    @GeneratedValue
    @Column(name = "chat_id")
    private Long id;

    private LocalDateTime sendDate;

    @ManyToOne(fetch = LAZY)
    private Account sender;

    @ManyToOne(fetch = LAZY)
    private Account receiver;

    private Boolean confirmed = false;

    public Chat(Account sender, Account receiver) {
        sendDate = LocalDateTime.now();
        this.sender = sender;
        this.receiver = receiver;
        confirmed = false;
    }
}

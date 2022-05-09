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

    private Long roomId;

    private String content;

    private LocalDateTime sendDate;

    @ManyToOne(fetch = LAZY)
    private Account sender;

    private Boolean confirmed = false;

    public Chat(Account sender, Long roomId, String content, LocalDateTime sendDate) {
        this.sender = sender;
        this.roomId = roomId;
        this.content = content;
        this.sendDate = sendDate;
        confirmed = false;
    }

}

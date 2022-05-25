package com.project.market.modules.chat.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecordDto {
    private String content;

    private LocalDateTime sendDate;

    private Long senderId;

    private String senderNickName;

    private Boolean confirmed;

    public RecordDto(String content, LocalDateTime sendDate, Long senderId, String senderNickName, Boolean confirmed) {
        this.content = content;
        this.sendDate = sendDate;
        this.senderId = senderId;
        this.senderNickName = senderNickName;
        this.confirmed = confirmed;
    }
}

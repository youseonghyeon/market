package com.project.market.modules.chat.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {

    private Long senderId;

    private Long roomId;

    private String content;

    private LocalDateTime sendDate;
}

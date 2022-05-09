package com.project.market.modules.chat.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {

    private String senderId;

    private Long roomId;

    private String content;

    private LocalDateTime sendDate;
}

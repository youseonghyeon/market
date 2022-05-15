package com.project.market.modules.chat.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {

    private String senderId;

    private String roomName;

    private String content;

    private LocalDateTime sendDate;
}

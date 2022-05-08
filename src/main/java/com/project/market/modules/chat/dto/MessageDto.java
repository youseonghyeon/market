package com.project.market.modules.chat.dto;

import lombok.Data;

@Data
public class MessageDto {

    private String senderId;

    private Long roomId;

    private String content;
}

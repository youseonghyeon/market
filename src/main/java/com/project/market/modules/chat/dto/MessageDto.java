package com.project.market.modules.chat.dto;

import lombok.Data;

@Data
public class MessageDto {

    private String senderId;

    private String receiverId;

    private String content;
}

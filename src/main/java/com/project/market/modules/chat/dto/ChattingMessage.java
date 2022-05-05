package com.project.market.modules.chat.dto;

import lombok.Data;

@Data
public class ChattingMessage {

    private String accountId;
    private String room;
    private String content;
}

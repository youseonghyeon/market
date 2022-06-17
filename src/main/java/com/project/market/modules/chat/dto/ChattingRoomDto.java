package com.project.market.modules.chat.dto;

import com.project.market.modules.account.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChattingRoomDto {

    private Long roomId;

    private LocalDateTime sendDate;

    private Account client;


}

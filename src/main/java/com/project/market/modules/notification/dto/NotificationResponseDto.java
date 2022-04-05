package com.project.market.modules.notification.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDto {
    private Long itemId;
    private String subject;
    private String content;
    private LocalDateTime createdAt;
}

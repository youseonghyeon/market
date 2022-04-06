package com.project.market.modules.notification.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
public class NotificationResponseDto {
    private Long itemId;
    private String subject;
    private String content;
    private String createdAt;
    private boolean confirmed;

    public NotificationResponseDto(Long itemId, String subject, String content, LocalDateTime createdAt, boolean confirmed) {
        this.itemId = itemId;
        this.subject = subject;
        this.content = content;
        long betweenDay = ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
        if (betweenDay > 0) {
            this.createdAt = betweenDay + "일 전";
        } else {
            long betweenHour = ChronoUnit.HOURS.between(createdAt, LocalDateTime.now());
            if (betweenHour > 0) {
                this.createdAt = betweenHour + "시간 전";
            } else {
                this.createdAt = "방금 전";
            }
        }
        this.confirmed = confirmed;
    }
}

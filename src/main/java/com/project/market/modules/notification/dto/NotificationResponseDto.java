package com.project.market.modules.notification.dto;

import com.project.market.modules.notification.entity.Notification;
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

    public NotificationResponseDto(Notification notify) {
        this.itemId = notify.getItemId();
        this.subject = notify.getSubject();
        this.content = notify.getContent();
        long betweenDay = ChronoUnit.DAYS.between(notify.getCreatedAt(), LocalDateTime.now());
        if (betweenDay > 0) {
            this.createdAt = betweenDay + "일 전";
        } else {
            long betweenHour = ChronoUnit.HOURS.between(notify.getCreatedAt(), LocalDateTime.now());
            if (betweenHour > 0) {
                this.createdAt = betweenHour + "시간 전";
            } else {
                this.createdAt = "방금 전";
            }
        }
        this.confirmed = notify.isConfirmed();

    }
}

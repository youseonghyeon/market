package com.project.market.modules.notification.controller;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.notification.repository.NotificationRepository;
import com.project.market.modules.notification.service.NotificationService;
import com.project.market.modules.notification.dto.NotificationResponseDto;
import com.project.market.modules.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    @GetMapping("/notification")
    public Result notificationListForm(@CurrentAccount Account account) {
        List<NotificationResponseDto> dtoList = new ArrayList<>();
        int unconfirmedTotal = 0;

        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(account);
        for (Notification n : notifications) {
            dtoList.add(new NotificationResponseDto(n.getItemId(), n.getSubject(), n.getContent(), n.getCreatedAt(), n.isConfirmed()));
            if (!n.isConfirmed()) unconfirmedTotal++;
        }
        return new Result(unconfirmedTotal, dtoList);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int unconfirmedTotal;
        private List<T> data;
    }

    @GetMapping("/notification/confirm")
    public void notificationConfirm(@CurrentAccount Account account) {
        List<Notification> notifications = notificationRepository.findByRecipient(account);
        notificationService.confirm(notifications);
    }

}

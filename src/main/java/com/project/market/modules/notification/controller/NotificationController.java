package com.project.market.modules.notification.controller;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.notification.dao.NotificationRepository;
import com.project.market.modules.notification.dao.NotificationService;
import com.project.market.modules.notification.dto.NotificationResponseDto;
import com.project.market.modules.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    @GetMapping("/notification")
    @ResponseBody
    public Result notificationListForm(@CurrentAccount Account account) {
        List<NotificationResponseDto> dtoList = new ArrayList<>();
        int unconfirmedTotal = 0;

        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(account);
        for (Notification notification : notifications) {
            dtoList.add(modelMapper.map(notification, NotificationResponseDto.class));
            if (!notification.isConfirmed()) unconfirmedTotal++;
        }
        return new Result(unconfirmedTotal, dtoList);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int unconfirmedTotal;
        private List<T> data;
    }

    @GetMapping("/notification/total")
    @ResponseBody
    public String notificationTotal(@CurrentAccount Account account) {
        long total = notificationRepository.countByRecipientAndConfirmedFalse(account);
        return String.valueOf(total);
    }

}

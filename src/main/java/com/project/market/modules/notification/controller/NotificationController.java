package com.project.market.modules.notification.controller;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.notification.dao.NotificationRepository;
import com.project.market.modules.notification.dao.NotificationService;
import com.project.market.modules.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping("/notification")
    public String notificationListForm(@CurrentAccount Account account, Model model) {
        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(account);
        notificationService.confirm(notifications);
        model.addAttribute("notificationList", notifications);
        return "notification/list";
    }

    @GetMapping("/notification/total")
    @ResponseBody
    public String notificationTotal(@CurrentAccount Account account) {
        long total = notificationRepository.countByRecipientAndConfirmedFalse(account);
        return String.valueOf(total);
    }

}

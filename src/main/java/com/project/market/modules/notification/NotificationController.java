package com.project.market.modules.notification;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.notification.dao.NotificationRepository;
import com.project.market.modules.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping("/notification")
    public String notificationListForm(@CurrentAccount Account account, Model model) {
        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(account);
        model.addAttribute("notificationList", notifications);
        return "notification/list";
    }
}

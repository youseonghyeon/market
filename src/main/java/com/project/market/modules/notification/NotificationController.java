package com.project.market.modules.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    @GetMapping("/notification")
    public String notificationListForm() {

        return "notification/list";
    }
}

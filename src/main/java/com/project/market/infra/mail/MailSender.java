package com.project.market.infra.mail;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Item;

public interface MailSender {

    void send(String sendTo, String token);

    void sendNotification(Account recipient, Item item);
}

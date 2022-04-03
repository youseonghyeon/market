package com.project.market.infra.mail;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Item;

public interface MailSender {

    void sendTokenMail(Account recipient, String token);

    void sendNoticeMail(Account recipient, Item item);
}

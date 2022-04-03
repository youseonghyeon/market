package com.project.market.infra.mail;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("test")
@Component
public class MailSenderTest implements MailSender {

    @Override
    public void sendTokenMail(Account recipient, String token) {
        log.info("------------------------------------");
        log.info("sendTo={}", recipient.getUsername());
        log.info("token={}", token);
        log.info("------------------------------------");
    }

    @Override
    public void sendNoticeMail(Account recipient, Item item) {
        log.info("------------------------------------");
        log.info("sendTo={}", recipient.getEmail());
        log.info("item={}", item.getName());
        log.info("------------------------------------");
    }
}

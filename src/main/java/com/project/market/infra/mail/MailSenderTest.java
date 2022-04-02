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
    public void send(String sendTo, String token) {

        log.info("------------------------------------");
        log.info("sendTo={}", sendTo);
        log.info("token={}", token);
        log.info("------------------------------------");
    }

    @Override
    public void sendNotification(Account recipient, Item item) {

    }
}

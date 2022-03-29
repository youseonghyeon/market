package com.project.market.infra.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("test")
@Component
public class TokenMailSenderTest implements TokenMailSender {

    @Override
    public boolean send(String sendTo, String token) {

        log.info("------------------------------------");
        log.info("sendTo={}", sendTo);
        log.info("token={}", token);
        log.info("------------------------------------");
        return true;
    }
}

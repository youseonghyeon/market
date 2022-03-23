package com.project.market.infra.mail;

import org.springframework.stereotype.Component;

public interface TokenMailSender {

    void send(String sendTo, String token);
}

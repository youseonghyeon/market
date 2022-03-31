package com.project.market.infra.mail;

public interface TokenMailSender {

    void send(String sendTo, String token);
}

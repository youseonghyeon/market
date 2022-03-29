package com.project.market.infra.mail;

public interface TokenMailSender {

    boolean send(String sendTo, String token);
}

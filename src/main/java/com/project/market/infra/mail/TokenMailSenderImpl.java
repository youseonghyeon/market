package com.project.market.infra.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Slf4j
@Profile({"local", "dev"})
@Component
@RequiredArgsConstructor
public class TokenMailSenderImpl implements TokenMailSender  {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String sendFrom;
//    @Value("${host}")
//    String host;

    @Override
    public void send(String sendTo, String token) {
        String mailTitle = "비밀번호 찾기 메일 테스트";
        // String mailContent = host + "/help/confirm?token=" + token;
        String mailContent = "http://localhost:8080/help/confirm?token=" + token;

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setTo(sendTo);
                message.setFrom(sendFrom);
                message.setSubject(mailTitle);
                message.setText(mailContent, false);
            }
        };
        try {
            mailSender.send(preparator);
        } catch (MailException e) {
            log.error("메일 전송 실패");
        }
    }
}

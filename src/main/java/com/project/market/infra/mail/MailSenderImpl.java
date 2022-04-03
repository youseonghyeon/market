package com.project.market.infra.mail;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Profile({"local", "dev"})
@Component
@RequiredArgsConstructor
public class MailSenderImpl implements MailSender {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String sendFrom;

    @Async
    @Override
    public void sendTokenMail(Account recipient, String token) {
        String subject = "비밀번호 찾기 메일 테스트";
        String content = "http://localhost:8080/help/confirm?token=" + token;

        MimeMessagePreparator preparator = createPrepare(recipient.getEmail(), subject, content);
        sendMail(preparator);
    }

    @Async
    @Override
    public void sendNoticeMail(Account recipient, Item item) {
        String title = "관심 상품이 등록되었습니다.";
        String content = "상품명: " + item.getName();

        MimeMessagePreparator preparator = createPrepare(recipient.getEmail(), title, content);
        sendMail(preparator);
    }

    private MimeMessagePreparator createPrepare(String sendTo, String subject, String content) {
        return mimeMessage -> {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setTo(sendTo);
            message.setFrom(sendFrom);
            message.setSubject(subject);
            message.setText(content, false);
        };
    }

    private void sendMail(MimeMessagePreparator preparator) {
        try {
            mailSender.send(preparator);
        } catch (MailException e) {
            log.error("메일 전송 실패");
        }
    }
}

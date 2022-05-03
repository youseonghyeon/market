package com.project.market.modules.chat;

import com.project.market.infra.exception.NoPrincipalException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.security.AccountContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.http.WebSocket;
import java.security.Principal;
import java.util.*;

@Slf4j
@Component
public class ChatHandler extends TextWebSocketHandler {

    private static Map<String, WebSocketSession> map = new HashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 전송한 메시지 내용 == payload
//        String payload = message.getPayload();

        WebSocketSession admin = map.get("admin");
        // db에 저장
        if (admin != null) {
            session.sendMessage(message);
            if (!session.equals(admin)) {
                admin.sendMessage(message);
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Principal principal = session.getPrincipal();
        String name;
        if (principal != null) {
            name = session.getPrincipal().getName();
        } else {
            throw new NoPrincipalException("비정상 접근");
        }
        map.put(name, session);
        log.info("클라이언트[{}] 웹 소켓 접속", name);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Principal principal = session.getPrincipal();
        String name;
        if (principal != null) {
            name = session.getPrincipal().getName();
        } else {
            throw new NoPrincipalException("비정상 접근");
        }
        map.remove(name);
        log.info("클라이언트[{}] 웹 소켓 접속 해제", name);
    }
}

package com.example.sirius.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
public abstract class AbstractWebSocketHandler extends TextWebSocketHandler {
//    private static List<WebSocketSession> list = new ArrayList<>();
    private Queue<WebSocketSession> session_list = new ConcurrentLinkedQueue<>();  // thread-safe queue

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        synchronized (session_list) {
            for (WebSocketSession sess : new ArrayList<>(session_list)) {  // Avoid ConcurrentModificationException
                if (sess.isOpen()) {
                    sess.sendMessage(message);
                } else {
                    session_list.remove(sess);
                }
            }
        }

//        log.info(String.valueOf(session.getUri()));
        log.info("Received payload: {}, from session URI: {}", payload, session.getUri());

    }

    /* Client가 접속 시 호출되는 메서드 */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session_list.add(session);
        log.info("Client connected: Session ID = {}", session.getId());
    }

    /* Client가 접속 해제 시 호출되는 메서드드 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Client disconnected: Session ID = {}, Status = {}", session.getId(), status);
        session_list.remove(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Error occurred for session: {}", session.getId(), exception);
    }

}

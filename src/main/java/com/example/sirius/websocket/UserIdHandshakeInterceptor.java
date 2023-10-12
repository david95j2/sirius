package com.example.sirius.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class UserIdHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
//        String path = request.getURI().getPath();
//        String[] pathSegments = path.split("/");
//        if(pathSegments.length > 1) {
//            String loginId = pathSegments[1]; // login_id 추출
//            attributes.put("login_id", loginId); // WebSocket 세션에 login_id 추가
//        }
//
//        // HttpSession을 WebSocketSession의 속성으로 추가
//        if (request instanceof ServletServerHttpRequest) {
//            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
//            HttpSession httpSession = servletRequest.getServletRequest().getSession();
//            attributes.put("HTTP_SESSION", httpSession); // WebSocket 세션에 HTTP_SESSION 추가
//        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // 필요한 후처리를 여기에 작성
    }
}
